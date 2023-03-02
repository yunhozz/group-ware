package com.mailservice.persistence.repository;

import com.mailservice.persistence.MailFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailFileRepository extends JpaRepository<MailFile, Long> {
}