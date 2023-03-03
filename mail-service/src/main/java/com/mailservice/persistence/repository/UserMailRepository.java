package com.mailservice.persistence.repository;

import com.mailservice.persistence.entity.mail.UserMail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMailRepository extends JpaRepository<UserMail, Long> {
}