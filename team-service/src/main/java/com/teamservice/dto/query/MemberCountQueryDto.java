package com.teamservice.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberCountQueryDto {

    private Long id;
    private Long teamId;

    @QueryProjection
    public MemberCountQueryDto(Long id, Long teamId) {
        this.id = id;
        this.teamId = teamId;
    }
}