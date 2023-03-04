package com.mailservice.persistence.repository;

import com.mailservice.persistence.entity.MailFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailFileRepository extends JpaRepository<MailFile, Long> {

    Optional<MailFile> findByFileId(String fileId);
}