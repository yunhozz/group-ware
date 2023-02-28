package com.teamservice.persistence.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamservice.dto.query.MemberCountQueryDto;
import com.teamservice.dto.query.QMemberCountQueryDto;
import com.teamservice.dto.query.QTeamQueryDto;
import com.teamservice.dto.query.QTeamSimpleQueryDto;
import com.teamservice.dto.query.TeamQueryDto;
import com.teamservice.dto.query.TeamSimpleQueryDto;
import com.teamservice.dto.response.TeamUserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.teamservice.persistence.QTeam.team;
import static com.teamservice.persistence.QTeamUser.teamUser;

@Repository
@RequiredArgsConstructor
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<TeamQueryDto> findTeamById(Long id) {
        TeamQueryDto teamDto = queryFactory
                .select(new QTeamQueryDto(
                        team.id,
                        team.name,
                        team.imageUrl,
                        team.createdAt,
                        team.modifiedAt
                ))
                .from(team)
                .where(team.id.eq(id))
                .fetchOne();

        List<TeamUserResponseDto> teamUserDtoList = queryFactory
                .select(Projections.constructor(
                        TeamUserResponseDto.class,
                        team.id,
                        teamUser.userId
                ))
                .from(teamUser)
                .join(teamUser.team, team)
                .where(team.id.eq(id))
                .fetch();

        teamDto.setTeamUserList(teamUserDtoList);
        return Optional.of(teamDto);
    }

    @Override
    public Slice<TeamSimpleQueryDto> findTeamSlice(Long cursorId, Pageable pageable) {
        List<TeamSimpleQueryDto> teams = queryFactory
                .select(new QTeamSimpleQueryDto(
                        team.id,
                        team.name,
                        team.imageUrl
                ))
                .from(team)
                .where(teamIdLt(cursorId))
                .orderBy(team.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<Long> teamIds = teams.stream()
                .map(TeamSimpleQueryDto::getId)
                .toList();

        List<MemberCountQueryDto> memberCountList = queryFactory
                .select(new QMemberCountQueryDto(
                        teamUser.id,
                        team.id
                ))
                .from(teamUser)
                .join(teamUser.team, team)
                .where(team.id.in(teamIds))
                .fetch();

        Map<Long, List<MemberCountQueryDto>> memberCountListMap = memberCountList.stream()
                .collect(Collectors.groupingBy(MemberCountQueryDto::getTeamId));
        teams.forEach(teamSimpleQueryDto -> {
            Long teamId = teamSimpleQueryDto.getId();
            List<MemberCountQueryDto> memberCount = memberCountListMap.get(teamId);

            if (memberCount != null) {
                int memberNum = memberCount.size();
                teamSimpleQueryDto.setMemberNum(memberNum);
            } else {
                teamSimpleQueryDto.setMemberNum(0);
            }
        });

        boolean hasNext = false;
        if (teams.size() > pageable.getPageSize()) {
            teams.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(teams, pageable, hasNext);
    }

    private BooleanExpression teamIdLt(Long cursorId) {
        return cursorId != null ? team.id.lt(cursorId) : null;
    }
}