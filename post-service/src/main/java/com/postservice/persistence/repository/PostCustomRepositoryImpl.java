package com.postservice.persistence.repository;

import com.postservice.common.enums.PostType;
import com.postservice.dto.query.CommentCountQueryDto;
import com.postservice.dto.query.CommentQueryDto;
import com.postservice.dto.query.FileQueryDto;
import com.postservice.dto.query.PostDetailsQueryDto;
import com.postservice.dto.query.PostSimpleQueryDto;
import com.postservice.dto.query.QCommentCountQueryDto;
import com.postservice.dto.query.QCommentQueryDto;
import com.postservice.dto.query.QFileQueryDto;
import com.postservice.dto.query.QPostDetailsQueryDto;
import com.postservice.dto.query.QPostSimpleQueryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.postservice.persistence.QComment.comment;
import static com.postservice.persistence.QFileEntity.fileEntity;
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

        List<CommentQueryDto> commentDtoList = queryFactory
                .select(new QCommentQueryDto(
                        comment.id,
                        post.id,
                        comment.writerId,
                        comment.parent.id,
                        comment.content
                ))
                .from(comment)
                .join(comment.post, post)
                .where(post.id.eq(id))
                .orderBy(comment.createdAt.asc())
                .fetch();

        List<FileQueryDto> fileDtoList = queryFactory
                .select(new QFileQueryDto(
                        fileEntity.fileId,
                        post.id,
                        fileEntity.originalName
                ))
                .from(fileEntity)
                .join(fileEntity.post, post)
                .where(post.id.eq(id))
                .orderBy(fileEntity.createdAt.asc())
                .fetch();

        postDto.setComments(commentDtoList);
        postDto.setFiles(fileDtoList);
        return postDto;
    }

    @Override
    public List<PostSimpleQueryDto> getPostSimpleListByType(PostType postType) {
        List<PostSimpleQueryDto> postDtoList = queryFactory
                .select(new QPostSimpleQueryDto(
                        post.id,
                        post.title,
                        post.writerId,
                        post.view,
                        post.createdAt
                ))
                .from(post)
                .where(
                        postTypeEq(postType),
                        post.isDeleted.eq('N')
                )
                .orderBy(post.createdAt.desc())
                .limit(3)
                .fetch();

        List<Long> postIds = extractPostIds(postDtoList);
        List<CommentCountQueryDto> commentCountDtoList = findCommentCountDtoListByPostIds(postIds);
        setCommentNumOnPostDtoList(postDtoList, commentCountDtoList);

        return postDtoList;
    }

    @Override
    public Slice<PostSimpleQueryDto> getPostSimpleSliceByType(PostType postType, Long cursorId, Pageable pageable) {
        List<PostSimpleQueryDto> postDtoList = queryFactory
                .select(new QPostSimpleQueryDto(
                        post.id,
                        post.title,
                        post.writerId,
                        post.view,
                        post.createdAt
                ))
                .from(post)
                .where(
                        postTypeEq(postType),
                        postIdLt(cursorId),
                        post.isDeleted.eq('N')
                )
                .orderBy(post.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<Long> postIds = extractPostIds(postDtoList);
        List<CommentCountQueryDto> commentCountDtoList = findCommentCountDtoListByPostIds(postIds);
        setCommentNumOnPostDtoList(postDtoList, commentCountDtoList);

        boolean hasNext = false;
        if (postDtoList.size() > pageable.getPageSize()) {
            postDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(postDtoList, pageable, hasNext);
    }

    private List<Long> extractPostIds(List<PostSimpleQueryDto> postDtoList) {
        return postDtoList.stream()
                .map(PostSimpleQueryDto::getId)
                .toList();
    }

    private List<CommentCountQueryDto> findCommentCountDtoListByPostIds(List<Long> postIds) {
        return queryFactory
                .select(new QCommentCountQueryDto(
                        comment.id,
                        post.id
                ))
                .from(comment)
                .join(comment.post, post)
                .where(post.id.in(postIds))
                .fetch();
    }

    private void setCommentNumOnPostDtoList(List<PostSimpleQueryDto> postDtoList, List<CommentCountQueryDto> commentCountDtoList) {
        Map<Long, List<CommentCountQueryDto>> commentCountDtoListMap = commentCountDtoList.stream()
                .collect(Collectors.groupingBy(CommentCountQueryDto::getPostId));
        postDtoList.forEach(postSimpleQueryDto -> {
            List<CommentCountQueryDto> comments = commentCountDtoListMap.get(postSimpleQueryDto.getId());
            postSimpleQueryDto.setCommentNum(comments.size());
        });
    }

    private BooleanExpression postTypeEq(PostType postType) {
        return postType != null ? post.postType.eq(postType) : null;
    }

    private BooleanExpression postIdLt(Long cursorId) {
        return cursorId != null ? post.id.lt(cursorId) : null;
    }
}