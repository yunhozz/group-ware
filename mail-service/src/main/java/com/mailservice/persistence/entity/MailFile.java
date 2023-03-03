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
public class MailFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mail mail;

    private String fileId;

    private String originalName;

    private String savePath;

    @Builder
    private MailFile(Mail mail, String fileId, String originalName, String savePath) {
        this.mail = mail;
        this.fileId = fileId;
        this.originalName = originalName;
        this.savePath = savePath;
    }
}