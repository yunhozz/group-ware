package com.teamservice.interfaces;

import com.teamservice.application.TeamService;
import com.teamservice.common.util.RedisUtils;
import com.teamservice.dto.query.TeamQueryDto;
import com.teamservice.dto.query.TeamSimpleQueryDto;
import com.teamservice.dto.request.TeamRequestDto;
import com.teamservice.dto.request.TeamUpdateRequestDto;
import com.teamservice.dto.response.TeamUserResponseDto;
import com.teamservice.dto.response.UserBasicResponseDto;
import com.teamservice.dto.response.UserSimpleResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.teamservice.common.util.RedisUtils.MY_INFO_KEY;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final RedisUtils redisUtils;

    @GetMapping
    public ResponseEntity<Slice<TeamSimpleQueryDto>> getTeamSlice(@RequestParam(required = false) Long cursorId, Pageable pageable) {
        Slice<TeamSimpleQueryDto> teamSlice = teamService.findSimpleSliceDto(cursorId, pageable);
        return ResponseEntity.ok(teamSlice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamQueryDto> getTeamInfoById(@PathVariable Long id) {
        TeamQueryDto teamDto = teamService.findTeamInfoById(id);
        List<TeamUserResponseDto> teamUserDtoList = teamDto.getTeamUserList();
        List<String> userIds = new ArrayList<>() {{
            for (TeamUserResponseDto teamUserResponseDto : teamUserDtoList) {
                add(teamUserResponseDto.getUserId());
            }
        }};

        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/users/basic")
                .queryParam("userIds", userIds)
                .build().toUri();
        ResponseEntity<Map<String, UserBasicResponseDto>> userData =
                restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        for (TeamUserResponseDto teamUserResponseDto : teamUserDtoList) {
            UserBasicResponseDto userInfo = userData.getBody().get(teamUserResponseDto.getUserId());
            teamUserResponseDto.setUserInfo(userInfo);
        }

        return ResponseEntity.ok(teamDto);
    }

    @PostMapping
    public ResponseEntity<Long> createTeam(@Valid @RequestBody TeamRequestDto teamRequestDto) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        Long teamId = teamService.makeTeam(myInfo.getUserId(), teamRequestDto);
        return new ResponseEntity<>(teamId, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/info")
    public ResponseEntity<String> updateInfoOfTeam(@PathVariable Long id, @Valid @RequestBody TeamUpdateRequestDto teamUpdateRequestDto) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        teamService.updateTeamInfo(id, myInfo.getUserId(), teamUpdateRequestDto);
        return new ResponseEntity<>("업데이트를 완료하였습니다.", HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/leader")
    public ResponseEntity<String> changeLeaderOfTeam(@PathVariable("id") Long teamId, @RequestParam String leaderId) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        teamService.changeLeader(teamId, myInfo.getUserId(), leaderId);
        return new ResponseEntity<>("팀장을 교체하였습니다.", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        teamService.deleteRequestListByTeamId(id);
        teamService.deleteTeam(id, myInfo.getUserId());
        return new ResponseEntity<>("삭제가 완료되었습니다.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{teamId}/member/{userId}")
    public ResponseEntity<String> withdrawMember(@PathVariable Long teamId, @PathVariable String userId) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        teamService.withdrawMember(teamId, myInfo.getUserId(), userId);
        return new ResponseEntity<>("해당 팀원을 추방하였습니다.", HttpStatus.NO_CONTENT);
    }

    private UserSimpleResponseDto getMyInfoFromRedis() {
        try {
            return redisUtils.getData(MY_INFO_KEY, UserSimpleResponseDto.class);
        } catch (Exception e) {
            throw new IllegalStateException(e.getLocalizedMessage());
        }
    }
}