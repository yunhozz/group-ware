package com.mailservice.persistence.repository;

import com.mailservice.persistence.entity.mail.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository<T extends Mail> extends JpaRepository<T, Long>, MailRepositoryCustom {
}