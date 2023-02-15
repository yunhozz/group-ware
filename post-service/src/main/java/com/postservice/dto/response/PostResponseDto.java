package com.postservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.postservice.common.enums.PostType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostResponseDto {

    private Long id;
    private Long teamId;
    @JsonIgnore
    private String writerId;
    private String title;
    private String content;
    private PostType postType; // MUST_READ, NOTICE, REPORT
    private Integer view;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private UserSimpleResponseDto userInfo;
    private List<CommentResponseDto> comments;

    @QueryProjection
    public PostResponseDto(Long id, Long teamId, String writerId, String title, String content, PostType postType, Integer view, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.teamId = teamId;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.view = view;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public void setUserInfo(UserSimpleResponseDto userInfo) {
        this.userInfo = userInfo;
    }

    public void setComments(List<CommentResponseDto> comments) {
        this.comments = comments;
    }
}