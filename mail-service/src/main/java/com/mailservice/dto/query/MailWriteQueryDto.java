package com.mailservice.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mailservice.common.enums.SecurityRating;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MailWriteQueryDto {

    // MailWrite
    @JsonIgnore
    private Long id;
    private String writerEmail;
    private Boolean isImportant;

    // MailSecurity
    private SecurityRating rating;
    private LocalDateTime validity;

    @QueryProjection
    public MailWriteQueryDto(Long id, String writerEmail, Boolean isImportant, SecurityRating rating, LocalDateTime validity) {
        this.id = id;
        this.writerEmail = writerEmail;
        this.isImportant = isImportant;
        this.rating = rating;
        this.validity = validity;
    }
}