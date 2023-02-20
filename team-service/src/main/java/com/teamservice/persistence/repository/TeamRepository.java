package com.teamservice.persistence.repository;

import com.teamservice.persistence.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByLeaderId(String userId);
    boolean existsByName(String name);
}