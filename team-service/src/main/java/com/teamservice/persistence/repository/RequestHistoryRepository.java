package com.teamservice.persistence.repository;

import com.teamservice.persistence.RequestHistory;
import com.teamservice.persistence.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long>, RequestHistoryCustomRepository {

    boolean existsByTeamAndUserId(Team team, String userId);

    @Query("select r.id from RequestHistory r join r.team t where t.id = :teamId")
    List<Long> findIdsByTeamId(@Param("teamId") Long teamId);

    @Query("select r.id" +
            " from RequestHistory r" +
            " join r.team t" +
            " where t.id = :teamId and r.createdAt <= :threshold and r.status = cast('P' as char)")
    List<Long> findIdsByTeamIdAndThreeDaysBefore(@Param("teamId") Long teamId, @Param("threshold") LocalDateTime threshold);

    @Modifying(clearAutomatically = true)
    @Query("delete RequestHistory r where r.id in :ids")
    void deleteListByIds(@Param("ids") List<Long> ids);

    default void deleteListByTeamId(Long teamId) {
        List<Long> ids = findIdsByTeamId(teamId);
        deleteListByIds(ids);
    }

    default void deleteUserRequestsInThreeDaysBefore(Long teamId, LocalDateTime now) {
        List<Long> ids = findIdsByTeamIdAndThreeDaysBefore(teamId, now);
        deleteListByIds(ids);
    }
}