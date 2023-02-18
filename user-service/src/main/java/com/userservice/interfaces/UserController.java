package com.userservice.interfaces;

import com.userservice.common.annotation.HeaderToken;
import com.userservice.common.util.TokenParser;
import com.userservice.interfaces.dto.response.UserProfileResponseDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final RestTemplate restTemplate;
    private final TokenParser tokenParser;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyInfo(@HeaderToken String token) {
        Claims claims = tokenParser.execute(token);
        return restTemplate.getForEntity(URI.create("http://localhost:8000/api/auth/users/" + claims.getSubject()), UserProfileResponseDto.class);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@HeaderToken String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);
        return restTemplate.exchange(URI.create("http://localhost:8000/api/auth/logout"), HttpMethod.POST, new HttpEntity<>(headers), String.class);
    }
}