package com.authserver.interfaces;

import com.authserver.application.AuthService;
import com.authserver.common.annotation.HeaderToken;
import com.authserver.dto.request.JoinRequestDto;
import com.authserver.dto.request.LoginRequestDto;
import com.authserver.dto.response.TokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody JoinRequestDto joinRequestDto) {
        String userId = authService.join(joinRequestDto);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto, response);
        return new ResponseEntity<>(tokenResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@HeaderToken String token) {
        authService.logout(token);
        return new ResponseEntity<>("로그아웃이 완료되었습니다.", HttpStatus.CREATED);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> tokenReissue(@HeaderToken String refreshToken, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.tokenReissue(refreshToken, response);
        return new ResponseEntity<>(tokenResponseDto, HttpStatus.CREATED);
    }
}