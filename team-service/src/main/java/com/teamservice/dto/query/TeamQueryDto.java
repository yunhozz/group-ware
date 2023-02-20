package com.teamservice.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import com.teamservice.dto.response.TeamUserResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class TeamQueryDto {

    private Long id;
    private String name;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<TeamUserResponseDto> teamUserList;

    @QueryProjection
    public TeamQueryDto(Long id, String name, String imageUrl, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public void setTeamUserList(List<TeamUserResponseDto> teamUserList) {
        this.teamUserList = teamUserList;
    }
}