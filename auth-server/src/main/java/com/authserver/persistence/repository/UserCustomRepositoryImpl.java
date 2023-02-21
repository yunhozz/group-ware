package com.authserver.persistence.repository;

import com.authserver.dto.response.UserDataResponseDto;
import com.authserver.dto.response.UserSimpleResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.authserver.persistence.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserDataResponseDto> findUserList() {
        return queryFactory
                .select(Projections.constructor(
                        UserDataResponseDto.class,
                        user.id,
                        user.userId,
                        user.email,
                        user.name,
                        user.provider.stringValue(),
                        user.roles
                ))
                .from(user)
                .orderBy(user.id.desc())
                .fetch();
    }

    @Override
    public List<UserSimpleResponseDto> findUserSimpleInfoListByUserIds(List<String> userIds) {
        return queryFactory
                .select(Projections.constructor(
                        UserSimpleResponseDto.class,
                        user.userId,
                        user.roles
                ))
                .from(user)
                .where(user.userId.in(userIds))
                .fetch();
    }
}