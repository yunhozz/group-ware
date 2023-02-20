package com.teamservice.persistence.repository;

import com.teamservice.dto.query.TeamQueryDto;

import java.util.Optional;

public interface TeamCustomRepository {

    Optional<TeamQueryDto> findTeamById(Long id);
}