package com.teamservice.persistence.repository;

import com.teamservice.dto.query.RequestHistoryQueryDto;

import java.util.List;

public interface RequestHistoryCustomRepository {

    List<RequestHistoryQueryDto> findListByTeamIdAndLeaderId(Long teamId, String leaderId); // 팀에서 가입 요청 리스트 조회
    List<RequestHistoryQueryDto> findListByUserId(String userId); // 유저 각각의 요청 리스트 조회
}