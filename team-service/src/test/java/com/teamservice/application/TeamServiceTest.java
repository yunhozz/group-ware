package com.teamservice.application;

import com.teamservice.application.exception.AlreadyCreatedException;
import com.teamservice.application.exception.NotLeaderException;
import com.teamservice.application.exception.TeamNameDuplicateException;
import com.teamservice.dto.request.TeamRequestDto;
import com.teamservice.dto.request.TeamUpdateRequestDto;
import com.teamservice.persistence.Team;
import com.teamservice.persistence.repository.RequestHistoryRepository;
import com.teamservice.persistence.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.atLeastOnce;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks
    TeamService teamService;

    @Mock
    TeamRepository teamRepository;

    @Mock
    RequestHistoryRepository requestHistoryRepository;

    Team team;

    @BeforeEach
    void beforeEach() {
        team = Team.create("userId", "Test team", "test.jpg");
    }

    @Test
    @DisplayName("팀 생성")
    void makeTeam() throws Exception {
        // given
        String userId = "userId";
        TeamRequestDto teamRequestDto = new TeamRequestDto("Test team", "test.jpg");

        given(teamRepository.existsByLeaderId(anyString())).willReturn(false);
        given(teamRepository.existsByName(anyString())).willReturn(false);
        given(teamRepository.save(any(Team.class))).willReturn(Team.create(userId, teamRequestDto.getName(), teamRequestDto.getImageUrl()));

        // when
        Long result = teamService.makeTeam(userId, teamRequestDto);

        // then
        assertDoesNotThrow(() -> result);
        verify(teamRepository, atLeastOnce()).save(any(Team.class));
    }

    @Test
    @DisplayName("팀 생성시 이미 생성한 팀 존재")
    void makeTeamThrowAlreadyCreatedException() throws Exception {
        // given
        String userId = "userId";
        TeamRequestDto teamRequestDto = new TeamRequestDto("Test team", "test.jpg");

        given(teamRepository.existsByLeaderId(anyString())).willReturn(true);

        // then
        assertThrows(AlreadyCreatedException.class, () -> teamService.makeTeam(userId, teamRequestDto));
    }

    @Test
    @DisplayName("팀 생성시 이름 중복")
    void makeTeamThrowTeamNameDuplicateException() throws Exception {
        // given
        String userId = "userId";
        TeamRequestDto teamRequestDto = new TeamRequestDto("Test team", "test.jpg");

        given(teamRepository.existsByLeaderId(anyString())).willReturn(false);
        given(teamRepository.existsByName(anyString())).willReturn(true);

        // then
        assertThrows(TeamNameDuplicateException.class, () -> teamService.makeTeam(userId, teamRequestDto));
    }

    @Test
    @DisplayName("팀 정보 변경")
    void updateTeamInfo() throws Exception {
        // given
        String userId = "userId";
        Long teamId = 10L;
        TeamUpdateRequestDto teamUpdateRequestDto = new TeamUpdateRequestDto("update team", "update.jpg");

        given(teamRepository.findById(anyLong())).willReturn(Optional.of(team));

        // then
        assertDoesNotThrow(() -> teamService.updateTeamInfo(teamId, userId, teamUpdateRequestDto));
        assertThat(team.getName()).isEqualTo("update team");
        assertThat(team.getImageUrl()).isEqualTo("update.jpg");
    }

    @Test
    @DisplayName("팀 정보 변경시 리더가 아닌 유저가 변경 시도")
    void updateTeamInfoThrowNotLeaderException() throws Exception {
        // given
        String userId = "anonymousId";
        Long teamId = 10L;
        TeamUpdateRequestDto teamUpdateRequestDto = new TeamUpdateRequestDto("update team", "update.jpg");

        given(teamRepository.findById(anyLong())).willReturn(Optional.of(team));

        // then
        assertThrows(NotLeaderException.class, () -> teamService.updateTeamInfo(teamId, userId, teamUpdateRequestDto));
    }

    @Test
    @DisplayName("팀 삭제")
    void deleteTeam() throws Exception {
        // given
        String userId = "userId";
        Long teamId = 10L;

        given(teamRepository.findById(anyLong())).willReturn(Optional.of(team));
        willDoNothing().given(teamRepository).delete(team);
        willDoNothing().given(requestHistoryRepository).deleteListByTeamId(teamId);

        // then
        assertDoesNotThrow(() -> teamService.deleteTeam(teamId, userId));
        verify(teamRepository, atLeastOnce()).delete(any(Team.class));
    }
}