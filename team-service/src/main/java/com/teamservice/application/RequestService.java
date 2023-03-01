package com.teamservice.application;

import com.teamservice.application.exception.AlreadyJoinedException;
import com.teamservice.application.exception.NotUserRequestException;
import com.teamservice.application.exception.RequestNotFoundException;
import com.teamservice.application.exception.RequestOnGoingException;
import com.teamservice.dto.query.RequestHistoryQueryDto;
import com.teamservice.persistence.RequestHistory;
import com.teamservice.persistence.Team;
import com.teamservice.persistence.TeamUser;
import com.teamservice.persistence.repository.RequestHistoryRepository;
import com.teamservice.persistence.repository.TeamRepository;
import com.teamservice.persistence.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

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

        } else {
            requestHistoryRepository.delete(requestHistory);
        }
    }

    @Transactional
    public void cancelRequest(Long requestId, String userId) {
        RequestHistory requestHistory = findRequestHistory(requestId);
        validateUserRequest(requestId, userId);
        requestHistoryRepository.delete(requestHistory);
    }

    @Transactional
    public void deleteUserOldRequests(Long teamId) {
        requestHistoryRepository.deleteUserRequestsInThreeDaysBefore(teamId, LocalDateTime.now().minusDays(3)); // 3 일이 경과한 요청 리스트 삭제
    }

    @Transactional(readOnly = true)
    public List<RequestHistoryQueryDto> findListByTeamIdAndLeaderId(Long teamId, String userId) {
        return requestHistoryRepository.findListByTeamIdAndLeaderId(teamId, userId);
    }

    @Transactional(readOnly = true)
    public List<RequestHistoryQueryDto> findListByUserId(String userId) {
        return requestHistoryRepository.findListByUserId(userId);
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

    private void validateUserRequest(Long requestId, String userId) {
        if (!requestHistoryRepository.existsByIdAndUserId(requestId, userId)) {
            throw new NotUserRequestException();
        }
    }
}