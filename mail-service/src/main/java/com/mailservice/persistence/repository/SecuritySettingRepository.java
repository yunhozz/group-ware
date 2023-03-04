package com.mailservice.persistence.repository;

import com.mailservice.persistence.entity.SecuritySetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecuritySettingRepository extends JpaRepository<SecuritySetting, Long> {
}