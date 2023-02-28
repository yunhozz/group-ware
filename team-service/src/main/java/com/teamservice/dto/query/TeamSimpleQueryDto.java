package com.teamservice.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamSimpleQueryDto {

    private Long id;
    private String name;
    private String imageUrl;
    private Integer memberNum;

    @QueryProjection
    public TeamSimpleQueryDto(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void setMemberNum(Integer memberNum) {
        this.memberNum = memberNum;
    }
}