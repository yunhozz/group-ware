package com.authserver.persistence.repository;

import com.authserver.dto.response.UserDataResponseDto;
import com.authserver.dto.response.UserSimpleResponseDto;

import java.util.List;

public interface UserCustomRepository {

    List<UserDataResponseDto> findUserList();
    UserSimpleResponseDto findUserSimpleInfoByUserId(String userId);
    List<UserSimpleResponseDto> findUserSimpleInfoListByUserIds(List<String> userIds);
}