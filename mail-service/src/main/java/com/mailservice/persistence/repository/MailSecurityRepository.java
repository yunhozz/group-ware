package com.mailservice.persistence.repository;

import com.mailservice.persistence.entity.MailSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailSecurityRepository extends JpaRepository<MailSecurity, Long> {
}