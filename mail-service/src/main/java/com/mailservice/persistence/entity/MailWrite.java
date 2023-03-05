package com.mailservice.persistence.entity;

import com.mailservice.persistence.entity.mail.Mail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private MailSecurity mailSecurity;

    private boolean isImportant;

    @Builder
    private MailWrite(String writerEmail, String receiverEmail, Long teamId, Mail mail, MailSecurity mailSecurity, boolean isImportant) {
        this.writerEmail = writerEmail;
        this.receiverEmail = receiverEmail;
        this.teamId = teamId;
        this.mail = mail;
        this.mailSecurity = mailSecurity;
        this.isImportant = isImportant;
    }

    // 개인 메일
    public static MailWrite userWrite(String writerEmail, String receiverEmail, Mail mail, MailSecurity mailSecurity, boolean isImportant) {
        return MailWrite.builder()
                .writerEmail(writerEmail)
                .receiverEmail(receiverEmail)
                .teamId(null)
                .mail(mail)
                .mailSecurity(mailSecurity)
                .isImportant(isImportant)
                .build();
    }

    // 팀 전체 메일
    public static MailWrite teamLeaderWrite(String leaderEmail, Long teamId, Mail mail, MailSecurity mailSecurity, boolean isImportant) {
        return MailWrite.builder()
                .writerEmail(leaderEmail)
                .receiverEmail(null)
                .teamId(teamId)
                .mail(mail)
                .mailSecurity(mailSecurity)
                .isImportant(isImportant)
                .build();
    }
}