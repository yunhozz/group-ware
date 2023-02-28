package com.teamservice.persistence.repository;

import com.teamservice.dto.query.TeamQueryDto;
import com.teamservice.dto.query.TeamSimpleQueryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface TeamCustomRepository {

    Optional<TeamQueryDto> findTeamById(Long id);
    Slice<TeamSimpleQueryDto> findTeamSlice(Long cursorId, Pageable pageable);
}