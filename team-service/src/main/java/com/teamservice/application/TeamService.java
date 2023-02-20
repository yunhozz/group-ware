package com.teamservice.application;

import com.teamservice.application.exception.AlreadyCreatedException;
import com.teamservice.application.exception.AlreadyJoinedException;
import com.teamservice.application.exception.DeleteNotAllowedException;
import com.teamservice.application.exception.NotBelongToTeamException;
import com.teamservice.application.exception.NotLeaderException;
import com.teamservice.application.exception.TeamNameDuplicateException;
import com.teamservice.application.exception.TeamNotFoundException;
import com.teamservice.application.exception.UpdateNotAllowedException;
import com.teamservice.dto.query.TeamQueryDto;
import com.teamservice.dto.request.TeamRequestDto;
import com.teamservice.dto.request.TeamUpdateRequestDto;
import com.teamservice.persistence.Team;
import com.teamservice.persistence.TeamUser;
import com.teamservice.persistence.repository.TeamRepository;
import com.teamservice.persistence.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional
    public Long makeTeam(String userId, TeamRequestDto teamRequestDto) {
        validateOnCreateTeam(userId, teamRequestDto);
        Team team = Team.create(userId, teamRequestDto.getName(), teamRequestDto.getImageUrl());
        return teamRepository.save(team).getId(); // cascade persist: TeamUser
    }

    @Transactional
    public void joinTeam(Long teamId, String userId) {
        Team team = findTeam(teamId);
        validateAlreadyJoined(userId, team);
        TeamUser teamUser = new TeamUser(team, userId);
        teamUserRepository.save(teamUser);
    }

    @Transactional
    public void updateTeamInfo(Long teamId, String userId, TeamUpdateRequestDto teamUpdateRequestDto) {
        Team team = findTeam(teamId);
        validateOnUpdateTeam(userId, team);
        team.updateInfo(teamUpdateRequestDto.getName(), teamUpdateRequestDto.getImageUrl());
    }

    @Transactional
    public void changeLeader(Long teamId, String userId, String leaderId) {
        Team team = findTeam(teamId);
        validateOnUpdateTeam(userId, team);
        team.changeLeader(leaderId);
    }

    @Transactional
    public void deleteTeam(Long teamId, String userId) {
        Team team = findTeam(teamId);
        validateOnDeleteTeam(userId, team);
        teamRepository.delete(team); // cascade delete: TeamUser list
    }

    @Transactional
    public void withdrawFromTeam(Long teamId, String userId) {
        Team team = teamRepository.getReferenceById(teamId);
        TeamUser teamUser = teamUserRepository.findByTeamAndUserId(team, userId)
                .orElseThrow(NotBelongToTeamException::new);
        teamUserRepository.delete(teamUser);
    }

    @Transactional(readOnly = true)
    public TeamQueryDto findTeamInfoById(Long id) {
        return teamRepository.findTeamById(id)
                .orElseThrow(TeamNotFoundException::new);
    }

    private Team findTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);
    }

    private void validateOnCreateTeam(String userId, TeamRequestDto teamRequestDto) {
        if (teamRepository.existsByLeaderId(userId)) {
            throw new AlreadyCreatedException();
        }

        if (teamRepository.existsByName(teamRequestDto.getName())) {
            throw new TeamNameDuplicateException();
        }
    }

    private void validateAlreadyJoined(String userId, Team team) {
        if (teamUserRepository.existsByTeamAndUserId(team, userId)) {
            throw new AlreadyJoinedException();
        }
    }

    private static void validateOnUpdateTeam(String userId, Team team) {
        if (!team.isLeader(userId)) {
            throw new NotLeaderException();
        }

        if (team.isModifiedLowerThanOneDayBefore()) {
            throw new UpdateNotAllowedException();
        }
    }

    private static void validateOnDeleteTeam(String userId, Team team) {
        if (!team.isLeader(userId)) {
            throw new NotLeaderException();
        }

        if (team.isCreatedLowerThanThreeDaysBefore()) {
            throw new DeleteNotAllowedException();
        }
    }
}