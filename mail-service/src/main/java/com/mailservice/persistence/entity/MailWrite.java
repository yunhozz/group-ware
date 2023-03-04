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

    private String writerEmail;

    private String receiverEmail;

    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mail mail;

    private boolean isImportant;

    @Builder
    private MailWrite(String writerEmail, String receiverEmail, Long teamId, Mail mail, SecuritySetting securitySetting, boolean isImportant) {
        this.writerEmail = writerEmail;
        this.receiverEmail = receiverEmail;
        this.teamId = teamId;
        this.mail = mail;
        this.isImportant = isImportant;
    }

    // 개인 메일
    public static MailWrite userWrite(String writerEmail, String receiverEmail, Mail mail, SecuritySetting securitySetting, boolean isImportant) {
        return MailWrite.builder()
                .writerEmail(writerEmail)
                .receiverEmail(receiverEmail)
                .teamId(null)
                .mail(mail)
                .isImportant(isImportant)
                .build();
    }

    // 팀 전체 메일
    public static MailWrite teamLeaderWrite(String leaderEmail, Long teamId, Mail mail, SecuritySetting securitySetting, boolean isImportant) {
        return MailWrite.builder()
                .writerEmail(leaderEmail)
                .receiverEmail(null)
                .teamId(teamId)
                .mail(mail)
                .isImportant(isImportant)
                .build();
    }
}