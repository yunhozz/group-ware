package com.teamservice.persistence.repository;

import com.teamservice.persistence.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository {

    boolean existsByLeaderId(String userId);
    boolean existsByName(String name);

    @Query("select distinct t from Team t join fetch t.teamUserList tu where t.id = :id")
    Team findWithTeamUserById(@Param("id") Long id);
}