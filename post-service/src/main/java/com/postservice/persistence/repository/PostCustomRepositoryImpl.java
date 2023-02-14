package com.postservice.persistence.repository;

import com.postservice.dto.response.PostResponseDto;
import com.postservice.dto.response.QPostResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.postservice.persistence.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PostResponseDto getPostDetailsById(Long postId) {
        return queryFactory
                .select(new QPostResponseDto(
                        post.id,
                        post.teamId,
                        post.writerId,
                        post.title,
                        post.content,
                        post.postType,
                        post.view,
                        post.createdAt,
                        post.modifiedAt
                ))
                .from(post)
                .where(post.id.eq(postId))
                .fetchOne();
    }
}