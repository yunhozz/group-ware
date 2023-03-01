package com.teamservice.application;

import com.teamservice.application.exception.AlreadyCreatedException;
import com.teamservice.application.exception.DeleteNotAllowedException;
import com.teamservice.application.exception.NotBelongToTeamException;
import com.teamservice.application.exception.NotLeaderException;
import com.teamservice.application.exception.TeamNameDuplicateException;
import com.teamservice.application.exception.TeamNotFoundException;
import com.teamservice.application.exception.UpdateNotAllowedException;
import com.teamservice.dto.query.TeamQueryDto;
import com.teamservice.dto.query.TeamSimpleQueryDto;
import com.teamservice.dto.request.TeamRequestDto;
import com.teamservice.dto.request.TeamUpdateRequestDto;
import com.teamservice.persistence.Team;
import com.teamservice.persistence.TeamUser;
import com.teamservice.persistence.repository.RequestHistoryRepository;
import com.teamservice.persistence.repository.TeamRepository;
import com.teamservice.persistence.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final RequestHistoryRepository requestHistoryRepository;

    @Transactional
    public Long makeTeam(String userId, TeamRequestDto teamRequestDto) {
        validateOnCreateTeam(userId, teamRequestDto);
        Team team = Team.create(userId, teamRequestDto.getName(), teamRequestDto.getImageUrl());
        return teamRepository.save(team).getId(); // cascade persist: TeamUser
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
        Team team = teamRepository.findWithTeamUserById(teamId);
        validateOnDeleteTeam(userId, team);
        teamUserRepository.deleteAllInBatch(team.getTeamUserList());
        teamRepository.deleteById(teamId);
    }

    @Transactional
    public void deleteRequestListByTeamId(Long id) {
        if (!teamRepository.existsById(id)) throw new TeamNotFoundException();
        requestHistoryRepository.deleteListByTeamId(id);
    }

    @Transactional
    public void withdrawMember(Long teamId, String leaderId, String userId) {
        Team team = teamRepository.getReferenceById(teamId);
        validateOnUpdateTeam(leaderId, team);
        TeamUser teamUser = teamUserRepository.findByTeamAndUserId(team, userId)
                .orElseThrow(NotBelongToTeamException::new);
        teamUserRepository.delete(teamUser);
    }

    @Transactional(readOnly = true)
    public TeamQueryDto findTeamInfoById(Long id) {
        return teamRepository.findTeamById(id)
                .orElseThrow(TeamNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Slice<TeamSimpleQueryDto> findSimpleSliceDto(Long cursorId, Pageable pageable) {
        return teamRepository.findTeamSlice(cursorId, pageable);
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

    private void validateOnUpdateTeam(String userId, Team team) {
        if (!team.isLeader(userId)) {
            throw new NotLeaderException();
        }

        if (team.isModifiedLowerThanOneDayBefore()) {
            throw new UpdateNotAllowedException();
        }
    }

    private void validateOnDeleteTeam(String userId, Team team) {
        if (!team.isLeader(userId)) {
            throw new NotLeaderException();
        }

        if (team.isCreatedLowerThanThreeDaysBefore()) {
            throw new DeleteNotAllowedException();
        }
    }
}