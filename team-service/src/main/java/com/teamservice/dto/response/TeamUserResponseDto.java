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
    private UserSimpleResponseDto userInfo;

    public TeamUserResponseDto(Long teamId, String userId) {
        this.teamId = teamId;
        this.userId = userId;
    }

    public void setUserInfo(UserSimpleResponseDto userInfo) {
        this.userInfo = userInfo;
    }
}