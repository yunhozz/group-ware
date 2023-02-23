package com.teamservice.application;

import com.teamservice.persistence.RequestHistory;
import com.teamservice.persistence.Team;
import com.teamservice.persistence.TeamUser;
import com.teamservice.persistence.repository.RequestHistoryRepository;
import com.teamservice.persistence.repository.TeamRepository;
import com.teamservice.persistence.repository.TeamUserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.atLeastOnce;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    TeamRepository teamRepository;

    @Mock
    TeamUserRepository teamUserRepository;

    @Mock
    RequestHistoryRepository requestHistoryRepository;

    Team team;

    @BeforeEach
    void beforeEach() {
        team = Team.create("userId", "Test team", "test.jpg");
    }

    @Test
    @DisplayName("팀 참가 요청")
    void requestToTeam() throws Exception {
        // given
        String userId = "userId";
        Long teamId = 10L;

        given(teamRepository.getReferenceById(anyLong())).willReturn(team);
        given(teamUserRepository.existsByTeamAndUserId(any(Team.class), anyString())).willReturn(false);
        given(requestHistoryRepository.existsByTeamAndUserId(any(Team.class), anyString())).willReturn(false);
        given(requestHistoryRepository.save(any(RequestHistory.class))).willReturn(RequestHistory.create(userId, team));

        // when
        Long result = memberService.requestToTeam(teamId, userId);

        // then
        assertDoesNotThrow(() -> result);
        verify(requestHistoryRepository, atLeastOnce()).save(any(RequestHistory.class));
    }

    @Test
    @DisplayName("팀 참가 요청 승인")
    void responseToUserAccept() throws Exception {
        // given
        String userId = "userId";
        Long requestId = 100L;
        RequestHistory requestHistory = RequestHistory.create(userId, team);

        given(requestHistoryRepository.findById(anyLong())).willReturn(Optional.of(requestHistory));
        given(teamUserRepository.save(any(TeamUser.class))).willReturn(new TeamUser(team, userId));

        // then
        assertDoesNotThrow(() -> memberService.responseToUser(requestId, true));
        assertThat(requestHistory.getStatus()).isEqualTo('Y');
        verify(teamUserRepository, atLeastOnce()).save(any(TeamUser.class));
    }

    @Test
    @DisplayName("팀 참가 요청 거절")
    void responseToUserDeny() throws Exception {
        // given
        String userId = "userId";
        Long requestId = 100L;
        RequestHistory requestHistory = RequestHistory.create(userId, team);

        given(requestHistoryRepository.findById(anyLong())).willReturn(Optional.of(requestHistory));
        willDoNothing().given(requestHistoryRepository).delete(requestHistory);

        // then
        assertDoesNotThrow(() -> memberService.responseToUser(requestId, false));
        verify(requestHistoryRepository, atLeastOnce()).delete(any(RequestHistory.class));
    }

    @Test
    @DisplayName("요청 취소")
    void cancelRequest() throws Exception {
        // given
        String userId = "userId";
        Long requestId = 100L;
        RequestHistory requestHistory = RequestHistory.create(userId, team);

        given(requestHistoryRepository.findById(anyLong())).willReturn(Optional.of(requestHistory));
        willDoNothing().given(requestHistoryRepository).delete(requestHistory);

        // then
        assertDoesNotThrow(() -> memberService.cancelRequest(requestId));
        verify(requestHistoryRepository, atLeastOnce()).delete(any(RequestHistory.class));
    }

    @Test
    @DisplayName("팀 탈퇴")
    void withdrawMember() throws Exception {
        // given
        String userId = "userId";
        Long teamId = 10L;
        TeamUser teamUser = new TeamUser(team, userId);

        given(teamRepository.getReferenceById(anyLong())).willReturn(team);
        given(teamUserRepository.findByTeamAndUserId(any(Team.class), anyString())).willReturn(Optional.of(teamUser));
        willDoNothing().given(teamUserRepository).delete(teamUser);

        // then
        assertDoesNotThrow(() -> memberService.withdrawMember(teamId, userId));
        verify(teamUserRepository, atLeastOnce()).delete(any(TeamUser.class));
    }
}