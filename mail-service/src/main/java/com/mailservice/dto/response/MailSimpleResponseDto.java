package com.mailservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mailservice.dto.query.MailWriteQueryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MailSimpleResponseDto {

    private Long mailId;
    @JsonIgnore
    private Long mailWriteId;
    private String title;
    private Boolean isRed;
    private LocalDateTime createdAt;

    private MailWriteQueryDto extraInfo;

    public MailSimpleResponseDto(Long mailId, Long mailWriteId, String title, Boolean isRed, LocalDateTime createdAt) {
        this.mailId = mailId;
        this.mailWriteId = mailWriteId;
        this.title = title;
        this.isRed = isRed;
        this.createdAt = createdAt;
    }

    public void setExtraInfo(MailWriteQueryDto extraInfo) {
        this.extraInfo = extraInfo;
    }
}