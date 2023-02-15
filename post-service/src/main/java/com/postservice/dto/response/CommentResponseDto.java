package com.postservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;
    private Long postId;
    @JsonIgnore
    private String writerId;
    private Long parentId;
    private String content;
    private UserSimpleResponseDto userInfo;

    public CommentResponseDto(Long id, Long postId, String writerId, Long parentId, String content) {
        this.id = id;
        this.postId = postId;
        this.writerId = writerId;
        this.parentId = parentId;
        this.content = content;
    }

    public void setUserInfo(UserSimpleResponseDto userInfo) {
        this.userInfo = userInfo;
    }
}