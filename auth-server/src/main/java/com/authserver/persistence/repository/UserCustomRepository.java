package com.authserver.persistence.repository;

import com.authserver.dto.response.UserDataResponseDto;

import java.util.List;

public interface UserCustomRepository {

    List<UserDataResponseDto> findUserList();
}