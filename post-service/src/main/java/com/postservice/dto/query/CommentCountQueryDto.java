package com.postservice.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCountQueryDto {

    private Long id;
    private Long postId;

    @QueryProjection
    public CommentCountQueryDto(Long id, Long postId) {
        this.id = id;
        this.postId = postId;
    }
}