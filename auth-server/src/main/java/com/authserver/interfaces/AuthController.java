package com.authserver.interfaces;

import com.authserver.application.AuthService;
import com.authserver.dto.request.JoinRequestDto;
import com.authserver.dto.request.LoginRequestDto;
import com.authserver.dto.response.TokenResponseDto;
import com.authserver.dto.response.UserDataResponseDto;
import com.authserver.dto.response.UserProfileResponseDto;
import com.authserver.dto.response.UserSimpleResponseDto;
import com.authserver.persistence.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserDataResponseDto>> getUserListInfo() {
        List<UserDataResponseDto> userDataResponseDtoList = userRepository.findUserList();
        return ResponseEntity.ok(userDataResponseDtoList);
    }

    @GetMapping("/users/{userId}/simple")
    public ResponseEntity<UserSimpleResponseDto> getUserSimpleInfo(@PathVariable String userId) {
        UserSimpleResponseDto userSimpleResponseDto = userRepository.findUserSimpleInfoByUserId(userId);
        return ResponseEntity.ok(userSimpleResponseDto);
    }

    @GetMapping("/users/simple")
    public ResponseEntity<List<UserSimpleResponseDto>> getUserSimpleInfoList(@RequestParam List<String> userIds) {
        List<UserSimpleResponseDto> userSimpleResponseDtoList = userRepository.findUserSimpleInfoListByUserIds(userIds);
        return ResponseEntity.ok(userSimpleResponseDtoList);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable String userId) {
        UserProfileResponseDto userProfileResponseDto = authService.findUserInfoByUserId(userId);
        return ResponseEntity.ok(userProfileResponseDto);
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinRequestDto joinRequestDto) {
        String userId = authService.join(joinRequestDto);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto, response);
        return new ResponseEntity<>(tokenResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        authService.logout(token);
        return new ResponseEntity<>("로그아웃이 완료되었습니다.", HttpStatus.CREATED);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> tokenReissue(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String refreshToken, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.tokenReissue(refreshToken, response);
        return new ResponseEntity<>(tokenResponseDto, HttpStatus.CREATED);
    }
}