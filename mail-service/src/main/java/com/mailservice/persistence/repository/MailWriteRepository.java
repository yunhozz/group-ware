package com.mailservice.persistence.repository;

import com.mailservice.persistence.entity.MailWrite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailWriteRepository extends JpaRepository<MailWrite, Long> {
}