package com.authserver.persistence.repository;

import com.authserver.dto.response.QUserDataResponseDto;
import com.authserver.dto.response.UserDataResponseDto;
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
                .select(new QUserDataResponseDto(
                        user.id,
                        user.userId,
                        user.email,
                        user.name,
                        user.provider.stringValue(),
                        user.role.stringValue()
                ))
                .from(user)
                .orderBy(user.id.desc())
                .fetch();
    }
}