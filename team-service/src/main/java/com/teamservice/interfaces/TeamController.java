package com.teamservice.interfaces;

import com.teamservice.application.TeamService;
import com.teamservice.dto.query.TeamQueryDto;
import com.teamservice.dto.response.TeamUserResponseDto;
import com.teamservice.dto.response.UserSimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

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
        URI uri = UriComponentsBuilder.fromUriString("localhost:8000/api/users/simple")
                .queryParam("userIds", userIds)
                .build().toUri();
        ResponseEntity<Map<String, UserSimpleResponseDto>> userData =
                restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        for (TeamUserResponseDto teamUserResponseDto : teamUserDtoList) {
            UserSimpleResponseDto userInfo = userData.getBody().get(teamUserResponseDto.getUserId());
            teamUserResponseDto.setUserInfo(userInfo);
        }

        return ResponseEntity.ok(teamDto);
    }
}