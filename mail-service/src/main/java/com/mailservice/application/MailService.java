package com.mailservice.application;

import com.mailservice.application.exception.EmailNotFoundException;
import com.mailservice.application.exception.EmailSendFailException;
import com.mailservice.application.exception.FileDownloadFailException;
import com.mailservice.application.exception.FileNotFoundException;
import com.mailservice.application.exception.FileUploadFailException;
import com.mailservice.common.enums.MailType;
import com.mailservice.common.enums.MailValidation;
import com.mailservice.common.enums.ReadStatus;
import com.mailservice.dto.request.MailWriteRequestDto;
import com.mailservice.dto.response.MailResponseDto;
import com.mailservice.dto.response.MailSimpleResponseDto;
import com.mailservice.persistence.entity.MailFile;
import com.mailservice.persistence.entity.MailSecurity;
import com.mailservice.persistence.entity.MailWrite;
import com.mailservice.persistence.entity.mail.BasicMail;
import com.mailservice.persistence.entity.mail.ImportantMail;
import com.mailservice.persistence.entity.mail.Mail;
import com.mailservice.persistence.repository.MailFileRepository;
import com.mailservice.persistence.repository.MailRepository;
import com.mailservice.persistence.repository.MailWriteRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MailRepository<Mail> mailRepository;
    private final MailWriteRepository mailWriteRepository;
    private final MailFileRepository mailFileRepository;
    private final JavaMailSender mailSender;

    @Value("${file.directory}")
    private String fileDir;

    @Transactional
    public Long writeUserMail(String writerEmail, String receiverEmail, MailWriteRequestDto mailWriteRequestDto) {
        Mail mail = saveMailByImportance(writerEmail, mailWriteRequestDto);
        LocalDateTime validity = null;

        if (mailWriteRequestDto.getValidation() != null) {
            validity = calculateMailValidation(mailWriteRequestDto.getValidation());
        }

        MailSecurity mailSecurity = new MailSecurity(mailWriteRequestDto.getRating(), validity);
        MailWrite mailWrite = MailWrite.userWrite(writerEmail, receiverEmail, mail, mailSecurity, mailWriteRequestDto.isImportant());
        mailWriteRepository.save(mailWrite); // cascade persist : MailSecurity

        uploadFiles(mail, mailWriteRequestDto.getFiles());
        sendMail(writerEmail, Set.of(receiverEmail), mailWriteRequestDto);
        return mail.getId();
    }

    @Transactional
    public Long writeTeamMail(String leaderEmail, Long teamId, Set<String> receiverEmails, MailWriteRequestDto mailWriteRequestDto) {
        Mail mail = saveMailByImportance(leaderEmail, mailWriteRequestDto);
        LocalDateTime validity = null;

        if (mailWriteRequestDto.getValidation() != null) {
            validity = calculateMailValidation(mailWriteRequestDto.getValidation());
        }

        MailSecurity mailSecurity = new MailSecurity(mailWriteRequestDto.getRating(), validity);
        MailWrite mailWrite = MailWrite.teamLeaderWrite(leaderEmail, teamId, mail, mailSecurity, mailWriteRequestDto.isImportant());
        mailWriteRepository.save(mailWrite); // cascade persist : MailSecurity

        uploadFiles(mail, mailWriteRequestDto.getFiles());
        sendMail(leaderEmail, receiverEmails, mailWriteRequestDto);
        return mail.getId();
    }

    @Transactional
    public void deleteMail(Long id) {
        Mail mail = mailRepository.findById(id)
                .orElseThrow(EmailNotFoundException::new);
        mail.delete();
    }

    @Transactional(readOnly = true)
    public Resource downloadFile(String fileId) {
        try {
            MailFile mailFile = mailFileRepository.findByFileId(fileId)
                    .orElseThrow(FileNotFoundException::new);
            Path path = Paths.get(mailFile.getSavePath());
            return new InputStreamResource(Files.newInputStream(path));

        } catch (IOException e) {
            throw new FileDownloadFailException();
        }
    }

    @Transactional(readOnly = true)
    public Page<MailSimpleResponseDto> findSimpleMailPageByTypeAndReadStatus(MailType mailType, ReadStatus readStatus, Pageable pageable) {
        mailRepository.deleteExpiredMailList(LocalDateTime.now()); // soft delete expired mail list
        return mailRepository.findSimpleMailPageByTypeAndReadStatus(mailType, readStatus, pageable);
    }

    @Transactional(readOnly = true)
    public MailResponseDto findMailDetailsById(Long id) {
        Mail mail = mailRepository.findById(id)
                .orElseThrow(EmailNotFoundException::new);
        mail.read();
        return mailRepository.findMailDetailsById(mail.getId());
    }

    private Mail saveMailByImportance(String writerEmail, MailWriteRequestDto mailWriteRequestDto) {
        Mail mail;
        if (mailWriteRequestDto.isImportant()) {
            mail = new ImportantMail(writerEmail, mailWriteRequestDto.getTitle(), mailWriteRequestDto.getContent());

        } else {
            mail = new BasicMail(writerEmail, mailWriteRequestDto.getTitle(), mailWriteRequestDto.getContent());
        }

        mailRepository.save(mail); // cascade persist : UserMail
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
                    throw new FileUploadFailException();
                }
            }

            mailFileRepository.saveAll(mailFileSet);
        }
    }

    private void sendMail(String senderEmail, Set<String> emailSet, MailWriteRequestDto mailWriteRequestDto) {
        try {
            for (String email : emailSet) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

                message.setFrom(senderEmail);
                message.setRecipients(Message.RecipientType.TO, email);
                message.setSubject(mailWriteRequestDto.getTitle());
                message.setText(mailWriteRequestDto.getContent());

                if (mailWriteRequestDto.getFiles() != null) {
                    for (MultipartFile file : mailWriteRequestDto.getFiles()) {
                        messageHelper.addAttachment(file.getOriginalFilename(), file);
                    }
                }

                mailSender.send(message);
            }

        } catch (MessagingException e) {
            throw new EmailSendFailException();
        }
    }

    private LocalDateTime calculateMailValidation(MailValidation validation) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validateDate = null;

        switch (validation.getDesc()) {
            case "하루" -> validateDate = now.plusDays(1);
            case "일주일" -> validateDate = now.plusWeeks(1);
            case "한달" -> validateDate = now.plusMonths(1);
        }

        return validateDate;
    }
}