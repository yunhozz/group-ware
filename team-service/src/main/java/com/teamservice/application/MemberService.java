package com.teamservice.application;

import com.teamservice.application.exception.AlreadyJoinedException;
import com.teamservice.application.exception.NotBelongToTeamException;
import com.teamservice.application.exception.NotLeaderException;
import com.teamservice.application.exception.RequestNotFoundException;
import com.teamservice.application.exception.RequestOnGoingException;
import com.teamservice.application.exception.TeamNotFoundException;
import com.teamservice.application.exception.UpdateNotAllowedException;
import com.teamservice.persistence.RequestHistory;
import com.teamservice.persistence.Team;
import com.teamservice.persistence.TeamUser;
import com.teamservice.persistence.repository.RequestHistoryRepository;
import com.teamservice.persistence.repository.TeamRepository;
import com.teamservice.persistence.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final RequestHistoryRepository requestHistoryRepository;

    @Transactional
    public Long requestToTeam(Long teamId, String userId) {
        Team team = teamRepository.getReferenceById(teamId);
        validateAlreadyJoinedOrRequest(userId, team);
        RequestHistory requestHistory = RequestHistory.create(userId, team);

        return requestHistoryRepository.save(requestHistory).getId();
    }

    @Transactional
    public void responseToUser(Long id, boolean flag) {
        RequestHistory requestHistory = findRequestHistory(id);

        if (flag) {
            requestHistory.accept();
            TeamUser teamUser = new TeamUser(requestHistory.getTeam(), requestHistory.getUserId());
            teamUserRepository.save(teamUser);

        } else requestHistoryRepository.delete(requestHistory);
    }

    @Transactional
    public void cancelRequest(Long id) {
        RequestHistory requestHistory = findRequestHistory(id);
        requestHistoryRepository.delete(requestHistory);
    }

    @Transactional
    public void changeLeader(Long teamId, String userId, String leaderId) {
        Team team = findTeam(teamId);
        validateOnUpdateTeam(userId, team);
        team.changeLeader(leaderId);
    }

    @Transactional
    public void withdrawMember(Long teamId, String userId) {
        Team team = teamRepository.getReferenceById(teamId);
        TeamUser teamUser = teamUserRepository.findByTeamAndUserId(team, userId)
                .orElseThrow(NotBelongToTeamException::new);
        teamUserRepository.delete(teamUser);
    }

    private Team findTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);
    }

    private RequestHistory findRequestHistory(Long requestId) {
        return requestHistoryRepository.findById(requestId)
                .orElseThrow(RequestNotFoundException::new);
    }

    private void validateAlreadyJoinedOrRequest(String userId, Team team) {
        if (teamUserRepository.existsByTeamAndUserId(team, userId)) {
            throw new AlreadyJoinedException();
        }

        if (requestHistoryRepository.existsByTeamAndUserId(team, userId)) {
            throw new RequestOnGoingException();
        }
    }

    private void validateOnUpdateTeam(String userId, Team team) {
        if (!team.isLeader(userId)) {
            throw new NotLeaderException();
        }

        if (team.isModifiedLowerThanOneDayBefore()) {
            throw new UpdateNotAllowedException();
        }
    }
}