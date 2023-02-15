package com.postservice.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.postservice.dto.response.UserSimpleResponseDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostSimpleQueryDto {

    private Long id;
    @JsonIgnore
    private String userId;
    private String title;
    private Integer view;
    private LocalDateTime createdAt;
    private Integer commentNum;
    private UserSimpleResponseDto userInfo;

    @QueryProjection
    public PostSimpleQueryDto(Long id, String userId, String title, Integer view, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.view = view;
        this.createdAt = createdAt;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public void setUserInfo(UserSimpleResponseDto userInfo) {
        this.userInfo = userInfo;
    }
}