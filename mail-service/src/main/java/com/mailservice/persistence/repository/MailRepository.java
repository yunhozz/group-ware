package com.mailservice.persistence.repository;

import com.mailservice.persistence.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<Mail, Long> {
}