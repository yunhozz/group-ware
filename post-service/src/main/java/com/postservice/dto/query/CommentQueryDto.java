package com.postservice.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.postservice.dto.response.UserSimpleResponseDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentQueryDto {

    private Long id;
    private Long postId;
    @JsonIgnore
    private String writerId;
    private Long parentId;
    private String content;
    @JsonIgnore
    private Boolean isMustDeleted;
    private UserSimpleResponseDto userInfo;

    @QueryProjection
    public CommentQueryDto(Long id, Long postId, String writerId, Long parentId, String content, Boolean isMustDeleted) {
        this.id = id;
        this.postId = postId;
        this.writerId = writerId;
        this.parentId = parentId;
        this.content = content;
        this.isMustDeleted = isMustDeleted;
    }

    public void setUserInfo(UserSimpleResponseDto userInfo) {
        this.userInfo = userInfo;
    }
}