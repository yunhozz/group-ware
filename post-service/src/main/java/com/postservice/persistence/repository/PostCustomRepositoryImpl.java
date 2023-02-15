package com.postservice.persistence.repository;

import com.postservice.dto.response.CommentResponseDto;
import com.postservice.dto.response.PostResponseDto;
import com.postservice.dto.response.QPostResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.postservice.persistence.QComment.comment;
import static com.postservice.persistence.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PostResponseDto getPostDetailsById(Long postId) {
        PostResponseDto postDto = queryFactory
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

        Long id = postDto.getId();

        List<CommentResponseDto> commentDtoList = queryFactory
                .select(Projections.constructor(
                        CommentResponseDto.class,
                        comment.id,
                        post.id,
                        comment.writerId,
                        comment.parent.id,
                        comment.content
                ))
                .from(comment)
                .join(comment.post, post)
                .where(post.id.eq(id))
                .orderBy(comment.createdAt.desc())
                .fetch();

        postDto.setComments(commentDtoList);
        return postDto;
    }
}