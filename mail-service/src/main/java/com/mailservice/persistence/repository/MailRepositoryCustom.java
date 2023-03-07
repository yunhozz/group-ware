package com.mailservice.persistence.repository;

import com.mailservice.common.enums.MailType;
import com.mailservice.common.enums.ReadStatus;
import com.mailservice.dto.response.MailResponseDto;
import com.mailservice.dto.response.MailSimpleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MailRepositoryCustom {

    Page<MailSimpleResponseDto> findSimpleMailPageByTypeAndReadStatus(MailType mailType, ReadStatus readStatus, Pageable pageable);
    MailResponseDto findMailDetailsById(Long id);
}