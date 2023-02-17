package com.postservice.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileQueryDto {

    private String fileId;
    private String originalName;
    private String savePath;

    @QueryProjection
    public FileQueryDto(String fileId, String originalName, String savePath) {
        this.fileId = fileId;
        this.originalName = originalName;
        this.savePath = savePath;
    }
}