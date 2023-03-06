package com.mailservice.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MailFileQueryDto {

    private String fileId;
    private String originalName;
    private String savePath;
    private LocalDateTime createdAt;

    @QueryProjection
    public MailFileQueryDto(String fileId, String originalName, String savePath, LocalDateTime createdAt) {
        this.fileId = fileId;
        this.originalName = originalName;
        this.savePath = savePath;
        this.createdAt = createdAt;
    }
}