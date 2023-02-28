package com.teamservice.persistence.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamservice.dto.query.QRequestHistoryQueryDto;
import com.teamservice.dto.query.RequestHistoryQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.teamservice.persistence.QRequestHistory.requestHistory;
import static com.teamservice.persistence.QTeam.team;

@Repository
@RequiredArgsConstructor
public class RequestHistoryCustomRepositoryImpl implements RequestHistoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RequestHistoryQueryDto> findListByTeamIdAndLeaderId(Long teamId, String leaderId) {
        return queryFactory
                .select(new QRequestHistoryQueryDto(
                        requestHistory.id,
                        requestHistory.userId,
                        Expressions.nullExpression(),
                        requestHistory.createdAt
                ))
                .from(requestHistory)
                .join(requestHistory.team, team)
                .where(
                        team.id.eq(teamId),
                        team.leaderId.eq(leaderId),
                        requestHistory.status.eq('P')
                )
                .orderBy(requestHistory.createdAt.asc())
                .fetch();
    }

    @Override
    public List<RequestHistoryQueryDto> findListByUserId(String userId) {
        return queryFactory
                .select(new QRequestHistoryQueryDto(
                        requestHistory.id,
                        Expressions.nullExpression(),
                        team.name,
                        requestHistory.createdAt
                ))
                .from(requestHistory)
                .join(requestHistory.team, team)
                .where(
                        requestHistory.userId.eq(userId),
                        requestHistory.status.eq('P')
                )
                .orderBy(requestHistory.createdAt.desc())
                .fetch();
    }
}