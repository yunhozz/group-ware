package com.authserver.persistence.repository;

import com.authserver.dto.response.QUserDataResponseDto;
import com.authserver.dto.response.QUserSimpleResponseDto;
import com.authserver.dto.response.UserDataResponseDto;
import com.authserver.dto.response.UserSimpleResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.authserver.persistence.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserDataResponseDto> findUserList() {
        return queryFactory
                .select(new QUserDataResponseDto(
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
    public Optional<UserSimpleResponseDto> findUserSimpleInfoByUserId(String userId) {
        UserSimpleResponseDto userSimpleDto = queryFactory
                .select(new QUserSimpleResponseDto(
                        user.userId,
                        user.name,
                        user.imageUrl
                ))
                .from(user)
                .where(user.userId.eq(userId))
                .fetchOne();

        return Optional.ofNullable(userSimpleDto);
    }

    @Override
    public List<UserSimpleResponseDto> findUserSimpleInfoListByUserIds(List<String> userIds) {
        return queryFactory
                .select(new QUserSimpleResponseDto(
                        user.userId,
                        user.name,
                        user.imageUrl
                ))
                .from(user)
                .where(user.userId.in(userIds))
                .fetch();
    }
}