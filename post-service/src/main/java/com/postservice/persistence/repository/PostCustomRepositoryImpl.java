package com.postservice.persistence.repository;

import com.postservice.common.enums.PostType;
import com.postservice.dto.query.CommentCountQueryDto;
import com.postservice.dto.query.PostDetailsQueryDto;
import com.postservice.dto.query.PostSimpleQueryDto;
import com.postservice.dto.query.QCommentCountQueryDto;
import com.postservice.dto.query.QPostDetailsQueryDto;
import com.postservice.dto.query.QPostSimpleQueryDto;
import com.postservice.dto.response.CommentResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.postservice.persistence.QComment.comment;
import static com.postservice.persistence.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PostDetailsQueryDto getPostDetailsById(Long postId) {
        PostDetailsQueryDto postDto = queryFactory
                .select(new QPostDetailsQueryDto(
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