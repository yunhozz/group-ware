package com.mailservice.application;

import com.mailservice.dto.request.MailWriteRequestDto;
import com.mailservice.persistence.entity.MailFile;
import com.mailservice.persistence.entity.MailWrite;
import com.mailservice.persistence.entity.SecuritySetting;
import com.mailservice.persistence.entity.mail.BasicMail;
import com.mailservice.persistence.entity.mail.ImportantMail;
import com.mailservice.persistence.entity.mail.Mail;
import com.mailservice.persistence.repository.MailFileRepository;
import com.mailservice.persistence.repository.MailRepository;
import com.mailservice.persistence.repository.MailWriteRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MailRepository<BasicMail> basicMailRepository;
    private final MailRepository<ImportantMail> importantMailRepository;
    private final MailWriteRepository mailWriteRepository;
    private final MailFileRepository mailFileRepository;
    private final JavaMailSender mailSender;

    @Value("${file.directory}")
    private String fileDir;

    @Transactional
    public Long writeUserMail(String writerEmail, String receiverEmail, MailWriteRequestDto mailWriteRequestDto, MultipartFile[] files) {
        boolean important = mailWriteRequestDto.isImportant();
        Mail mail = saveMailByImportance(writerEmail, mailWriteRequestDto, important);

        SecuritySetting securitySetting = new SecuritySetting(mailWriteRequestDto.getRating(), mailWriteRequestDto.getValidity());
        MailWrite mailWrite = MailWrite.userWrite(writerEmail, receiverEmail, mail, securitySetting, mailWriteRequestDto.isImportant());
        mailWriteRepository.save(mailWrite); // cascade persist : SecuritySetting

        uploadFiles(mail, files);
        sendMail(Set.of(receiverEmail));

        return mail.getId();
    }

    @Transactional
    public Long writeTeamMail(String leaderEmail, Long teamId, Set<String> receiverEmails, MailWriteRequestDto mailWriteRequestDto, MultipartFile[] files) {
        boolean important = mailWriteRequestDto.isImportant();
        Mail mail = saveMailByImportance(leaderEmail, mailWriteRequestDto, important);

        SecuritySetting securitySetting = new SecuritySetting(mailWriteRequestDto.getRating(), mailWriteRequestDto.getValidity());
        MailWrite mailWrite = MailWrite.teamLeaderWrite(leaderEmail, teamId, mail, securitySetting, mailWriteRequestDto.isImportant());
        mailWriteRepository.save(mailWrite); // cascade persist : SecuritySetting

        uploadFiles(mail, files);
        sendMail(receiverEmails);

        return mail.getId();
    }

    private Mail saveMailByImportance(String writerEmail, MailWriteRequestDto mailWriteRequestDto, boolean important) {
        Mail mail;
        if (important) {
            ImportantMail importantMail = new ImportantMail(writerEmail, mailWriteRequestDto.getTitle(), mailWriteRequestDto.getContent());
            importantMailRepository.save(importantMail); // cascade persist : UserMail
            mail = importantMail;

        } else {
            BasicMail basicMail = new BasicMail(writerEmail, mailWriteRequestDto.getTitle(), mailWriteRequestDto.getContent());
            basicMailRepository.save(basicMail); // cascade persist : UserMail
            mail = basicMail;
        }

        return mail;
    }

    private void uploadFiles(Mail mail, MultipartFile[] files) {
        if (files != null) {
            Set<MailFile> mailFileSet = new HashSet<>();
            for (MultipartFile file : files) {
                String fileId = UUID.randomUUID().toString();
                String originalName = file.getOriginalFilename();
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String savePath = fileDir + fileId + extension;

                MailFile mailFile = MailFile.builder()
                        .mail(mail)
                        .fileId(fileId)
                        .originalName(originalName)
                        .savePath(savePath)
                        .build();
                mailFileSet.add(mailFile);

                try {
                    file.transferTo(new File(savePath));
                } catch (IOException e) {
                    throw new IllegalStateException(e.getLocalizedMessage());
                }
            }

            mailFileRepository.saveAll(mailFileSet);
        }
    }

    private Resource downloadFile(String fileId) {
        try {
            MailFile mailFile = mailFileRepository.findByFileId(fileId)
                    .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
            Path path = Paths.get(mailFile.getSavePath());
            return new InputStreamResource(Files.newInputStream(path));

        } catch (IOException e) {
            throw new IllegalStateException(e.getLocalizedMessage());
        }
    }

    // TODO: 2023-03-04 파일 전송 메소드 작성
    private void sendMail(Set<String> mailSet) {
        MimeMessage message = mailSender.createMimeMessage();
    }
}