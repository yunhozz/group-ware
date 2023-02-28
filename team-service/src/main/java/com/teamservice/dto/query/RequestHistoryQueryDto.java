package com.teamservice.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import com.teamservice.dto.response.UserBasicResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestHistoryQueryDto {

    private Long id;
    @JsonIgnore
    private String userId;
    private String teamName;
    private LocalDateTime createdAt;
    private UserBasicResponseDto userInfo;

    @QueryProjection
    public RequestHistoryQueryDto(Long id, String userId, String teamName, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.teamName = teamName;
        this.createdAt = createdAt;
    }

    public void setUserInfo(UserBasicResponseDto userInfo) {
        this.userInfo = userInfo;
    }
}