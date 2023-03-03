package com.mailservice.persistence.entity;

import com.mailservice.persistence.entity.mail.Mail;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailWrite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writerId;

    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mail mail;

    private boolean isImportant;

    @Builder
    private MailWrite(String writerId, Long teamId, Mail mail, boolean isImportant) {
        this.writerId = writerId;
        this.teamId = teamId;
        this.mail = mail;
        this.isImportant = isImportant;
    }

    public static MailWrite userWrite(String writerId, Mail mail, boolean isImportant) {
        return MailWrite.builder()
                .writerId(writerId)
                .teamId(null)
                .mail(mail)
                .isImportant(isImportant)
                .build();
    }

    public static MailWrite teamLeaderWrite(String leaderId, Long teamId, Mail mail, boolean isImportant) {
        return MailWrite.builder()
                .writerId(leaderId)
                .teamId(teamId)
                .mail(mail)
                .isImportant(isImportant)
                .build();
    }
}