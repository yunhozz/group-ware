package com.mailservice.persistence.repository;

import com.mailservice.persistence.UserMail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMailRepository extends JpaRepository<UserMail, Long> {
}