package com.postservice.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileQueryDto {

    private String fileId;
    @JsonIgnore
    private Long postId;
    private String originalName;

    @QueryProjection
    public FileQueryDto(String fileId, Long postId, String originalName) {
        this.fileId = fileId;
        this.postId = postId;
        this.originalName = originalName;
    }
}