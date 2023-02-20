package com.authserver.persistence.repository;

import com.authserver.dto.response.UserDataResponseDto;
import com.authserver.dto.response.UserSimpleResponseDto;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {

    List<UserDataResponseDto> findUserList();
    Optional<UserSimpleResponseDto> findUserSimpleInfoByUserId(String userId);
    List<UserSimpleResponseDto> findUserSimpleInfoListByUserIds(List<String> userIds);
}