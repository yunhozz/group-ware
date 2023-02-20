package com.teamservice.persistence.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamservice.dto.query.QTeamQueryDto;
import com.teamservice.dto.query.TeamQueryDto;
import com.teamservice.dto.response.TeamUserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
}