package com.teamservice.persistence.repository;

import com.teamservice.persistence.Team;
import com.teamservice.persistence.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    Optional<TeamUser> findByTeamAndUserId(Team team, String userId);
    boolean existsByTeamAndUserId(Team team, String userId);
}