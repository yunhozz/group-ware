package com.teamservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamUserResponseDto {

    @JsonIgnore
    private Long teamId;
    @JsonIgnore
    private String userId;
    private UserBasicResponseDto userInfo;

    public TeamUserResponseDto(Long teamId, String userId) {
        this.teamId = teamId;
        this.userId = userId;
    }

    public void setUserInfo(UserBasicResponseDto userInfo) {
        this.userInfo = userInfo;
    }
}