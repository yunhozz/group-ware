package com.mailservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mailservice.dto.query.MailFileQueryDto;
import com.mailservice.dto.query.MailWriteQueryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class MailResponseDto {

    private Long mailId;
    @JsonIgnore
    private Long mailWriteId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    private MailWriteQueryDto extraInfo;
    private List<MailFileQueryDto> fileList;

    public MailResponseDto(Long mailId, Long mailWriteId, String title, String content, LocalDateTime createdAt) {
        this.mailId = mailId;
        this.mailWriteId = mailWriteId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public void setExtraInfo(MailWriteQueryDto extraInfo) {
        this.extraInfo = extraInfo;
    }

    public void setFileList(List<MailFileQueryDto> fileList) {
        this.fileList = fileList;
    }
}