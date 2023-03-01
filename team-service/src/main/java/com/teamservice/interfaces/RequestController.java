package com.teamservice.interfaces;

import com.teamservice.application.RequestService;
import com.teamservice.common.util.RedisUtils;
import com.teamservice.dto.query.RequestHistoryQueryDto;
import com.teamservice.dto.response.UserBasicResponseDto;
import com.teamservice.dto.response.UserSimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;
    private final RedisUtils redisUtils;

    @GetMapping
    public ResponseEntity<List<RequestHistoryQueryDto>> getRequestListByTeamOrUser(@RequestParam(required = false) Long teamId) {
        List<RequestHistoryQueryDto> requestDtoList;
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();

        if (teamId != null) { // 특정 팀의 유저 가입 요청 리스트 조회
            requestService.deleteUserOldRequests(teamId);
            requestDtoList = requestService.findListByTeamIdAndLeaderId(teamId, myInfo.getUserId());

            if (!requestDtoList.isEmpty()) {
                List<String> userIds = new ArrayList<>() {{
                    for (RequestHistoryQueryDto requestHistoryQueryDto : requestDtoList) {
                        String userId = requestHistoryQueryDto.getUserId();
                        add(userId);
                    }
                }};

                RestTemplate restTemplate = new RestTemplate();
                URI uri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/users/basic")
                        .queryParam("userIds", userIds)
                        .build().toUri();
                ResponseEntity<Map<String, UserBasicResponseDto>> userData =
                        restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

                for (RequestHistoryQueryDto requestHistoryQueryDto : requestDtoList) {
                    UserBasicResponseDto userInfo = userData.getBody().get(requestHistoryQueryDto.getUserId());
                    requestHistoryQueryDto.setUserInfo(userInfo);
                }
            }

        } else { // 특정 유저의 가입 요청 리스트 조회
            requestDtoList = requestService.findListByUserId(myInfo.getUserId());
        }

        return ResponseEntity.ok(requestDtoList);
    }

    @PostMapping
    public ResponseEntity<Long> joinRequestToTeam(@RequestParam Long teamId) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        Long requestId = requestService.requestToTeam(teamId, myInfo.getUserId());
        return new ResponseEntity<>(requestId, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> responseOnUserRequest(@PathVariable Long id, @RequestParam Boolean flag) {
        requestService.responseToUser(id, flag);
        return new ResponseEntity<>("요청을 처리하였습니다.", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelRequest(@PathVariable Long id) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        requestService.cancelRequest(id, myInfo.getUserId());
        return new ResponseEntity<>("취소가 완료되었습니다.", HttpStatus.NO_CONTENT);
    }

    private UserSimpleResponseDto getMyInfoFromRedis() {
        try {
            return redisUtils.getData(MY_INFO_KEY, UserSimpleResponseDto.class);
        } catch (Exception e) {
            throw new IllegalStateException(e.getLocalizedMessage());
        }
    }
}