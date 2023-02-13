package com.notificationservice.interfaces;

import com.notificationservice.application.NotificationService;
import com.notificationservice.common.annotation.HeaderToken;
import com.notificationservice.common.util.TokenParser;
import com.notificationservice.dto.request.NotificationRequestDto;
import com.notificationservice.dto.response.NotificationResponseDto;
import com.notificationservice.dto.response.NotificationSimpleResponseDto;
import com.notificationservice.interfaces.dto.UserSimpleResponseDto;
import com.notificationservice.persistence.repository.NotificationRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final TokenParser tokenParser;
    private final RestTemplate restTemplate;

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationDetails(@PathVariable Long id) {
        NotificationResponseDto notificationResponseDto = notificationService.getNotificationDto(id);
        return ResponseEntity.ok(notificationResponseDto);
    }

    @PostMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@HeaderToken String token, @RequestHeader(name = "Last-Event-Id", defaultValue = "") String lastEventId) {
        Claims claims = tokenParser.execute(token);
        SseEmitter emitter = notificationService.connect(claims.getSubject(), lastEventId);
        return new ResponseEntity<>(emitter, HttpStatus.CREATED);
    }

    @PostMapping("/send")
    public ResponseEntity<Long> sendMessageToUser(@HeaderToken String token, @Valid @RequestBody NotificationRequestDto notificationRequestDto) {
        Claims claims = tokenParser.execute(token);
        Long notificationId = notificationService.sendMessage(claims.getSubject(), notificationRequestDto);
        return new ResponseEntity<>(notificationId, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<Page<NotificationSimpleResponseDto>> getNotificationSimpleList(@HeaderToken String token, @RequestParam(required = false) Boolean check,
                                                                                         @RequestParam(required = false) Long lastId, Pageable pageable) {
        Claims claims = tokenParser.execute(token);
        Page<NotificationSimpleResponseDto> notificationSimpleDtoList = notificationRepository.findListByUserIdAndCheckStatus(claims.getSubject(), check, lastId, pageable);
        List<String> senderIds = new ArrayList<>() {{
            for (NotificationSimpleResponseDto notificationSimpleDto : notificationSimpleDtoList) {
                add(notificationSimpleDto.getSenderId());
            }
        }};

        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/auth/users/simple")
                .queryParam("userIds", senderIds)
                .build().toUri();
        ResponseEntity<List<UserSimpleResponseDto>> userSimpleDtoList = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        Map<String, List<UserSimpleResponseDto>> userSimpleDtoListMap = userSimpleDtoList.getBody().stream()
                .collect(Collectors.groupingBy(UserSimpleResponseDto::getUserId));

        notificationSimpleDtoList.forEach(notificationSimpleResponseDto -> {
            List<UserSimpleResponseDto> userList = userSimpleDtoListMap.get(notificationSimpleResponseDto.getSenderId());
            UserSimpleResponseDto userInfo = userList.get(0);
            notificationSimpleResponseDto.setUserInfo(userInfo);
        });

        return ResponseEntity.ok(notificationSimpleDtoList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return new ResponseEntity<>("삭제가 완료되었습니다.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/checked")
    public ResponseEntity<String> deleteNotificationListAlreadyChecked(@HeaderToken String token) {
        Claims claims = tokenParser.execute(token);
        notificationService.deleteNotificationsAlreadyChecked(claims.getSubject());
        return new ResponseEntity<>("읽은 알림을 삭제하였습니다.", HttpStatus.NO_CONTENT);
    }
}