package com.teamservice.persistence.repository;

import com.teamservice.persistence.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}