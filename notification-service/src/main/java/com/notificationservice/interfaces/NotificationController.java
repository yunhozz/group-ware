package com.notificationservice.interfaces;

import com.notificationservice.application.NotificationService;
import com.notificationservice.common.util.RedisUtils;
import com.notificationservice.dto.request.NotificationRequestDto;
import com.notificationservice.dto.response.NotificationResponseDto;
import com.notificationservice.dto.response.NotificationSimpleResponseDto;
import com.notificationservice.dto.response.UserBasicResponseDto;
import com.notificationservice.dto.response.UserSimpleResponseDto;
import com.notificationservice.persistence.repository.NotificationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.notificationservice.common.util.RedisUtils.MY_INFO_KEY;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final RedisUtils redisUtils;

    @GetMapping
    public ResponseEntity<Slice<NotificationSimpleResponseDto>> getNotificationSimpleList(@RequestParam(required = false) Boolean check,
                                                                                          @RequestParam(required = false) Long cursorId, Pageable pageable) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        Slice<NotificationSimpleResponseDto> notificationSimpleDtoList =
                notificationRepository.findSliceByUserIdAndCheckStatus(myInfo.getUserId(), check, cursorId, pageable);
        List<String> senderIds = new ArrayList<>() {{
            for (NotificationSimpleResponseDto notificationSimpleDto : notificationSimpleDtoList) {
                add(notificationSimpleDto.getSenderId());
            }
        }};

        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/users/basic")
                .queryParam("userIds", senderIds)
                .build().toUri();
        ResponseEntity<Map<String, UserBasicResponseDto>> userData =
                restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        for (NotificationSimpleResponseDto notificationSimpleResponseDto : notificationSimpleDtoList) {
            UserBasicResponseDto userInfo = userData.getBody().get(notificationSimpleResponseDto.getSenderId());
            notificationSimpleResponseDto.setUserInfo(userInfo);
        }

        return ResponseEntity.ok(notificationSimpleDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationDetails(@PathVariable Long id) {
        NotificationResponseDto notificationResponseDto = notificationService.getNotificationDto(id);
        return ResponseEntity.ok(notificationResponseDto);
    }

    @PostMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@RequestHeader(name = "Last-Event-Id", defaultValue = "") String lastEventId) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        SseEmitter emitter = notificationService.connect(myInfo.getUserId(), lastEventId);
        return new ResponseEntity<>(emitter, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<Long> sendMessageToUser(@Valid @RequestBody NotificationRequestDto notificationRequestDto) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        Long notificationId = notificationService.sendMessage(myInfo.getUserId(), notificationRequestDto);
        return new ResponseEntity<>(notificationId, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return new ResponseEntity<>("삭제가 완료되었습니다.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/checked")
    public ResponseEntity<String> deleteNotificationListAlreadyChecked() {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        notificationService.deleteNotificationsAlreadyChecked(myInfo.getUserId());
        return new ResponseEntity<>("읽은 알림을 삭제하였습니다.", HttpStatus.NO_CONTENT);
    }

    private UserSimpleResponseDto getMyInfoFromRedis() {
        try {
            return redisUtils.getData(MY_INFO_KEY, UserSimpleResponseDto.class);
        } catch (Exception e) {
            throw new IllegalStateException(e.getLocalizedMessage());
        }
    }
}