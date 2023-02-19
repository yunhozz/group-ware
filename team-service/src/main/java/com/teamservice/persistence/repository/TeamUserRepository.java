package com.teamservice.persistence.repository;

import com.teamservice.persistence.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
}