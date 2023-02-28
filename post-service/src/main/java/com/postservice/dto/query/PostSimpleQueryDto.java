package com.postservice.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.postservice.dto.response.UserBasicResponseDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostSimpleQueryDto {

    private Long id;
    @JsonIgnore
    private String writerId;
    private String title;
    private Integer view;
    private LocalDateTime createdAt;
    private Integer commentNum;
    private UserBasicResponseDto userInfo;

    @QueryProjection
    public PostSimpleQueryDto(Long id, String writerId, String title, Integer view, LocalDateTime createdAt) {
        this.id = id;
        this.writerId = writerId;
        this.title = title;
        this.view = view;
        this.createdAt = createdAt;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public void setUserInfo(UserBasicResponseDto userInfo) {
        this.userInfo = userInfo;
    }
}