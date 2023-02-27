package com.authserver.interfaces;

import com.authserver.application.AuthService;
import com.authserver.common.annotation.HeaderToken;
import com.authserver.dto.response.UserBasicResponseDto;
import com.authserver.dto.response.UserDataResponseDto;
import com.authserver.dto.response.UserProfileResponseDto;
import com.authserver.dto.response.UserSimpleResponseDto;
import com.authserver.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserDataResponseDto>> getUserListInfo() {
        List<UserDataResponseDto> userDataResponseDtoList = userRepository.findUserList();
        return ResponseEntity.ok(userDataResponseDtoList);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(@HeaderToken String token) {
        UserProfileResponseDto userProfileResponseDto = authService.findUserProfileByToken(token);
        return ResponseEntity.ok(userProfileResponseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable String userId) {
        UserProfileResponseDto userProfileResponseDto = authService.findUserProfileByUserId(userId);
        return ResponseEntity.ok(userProfileResponseDto);
    }

    @GetMapping("/{userId}/simple")
    public ResponseEntity<UserSimpleResponseDto> getUserSimpleInfo(@PathVariable String userId) {
        UserSimpleResponseDto userInfo = authService.findUserInfoByUserId(userId);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/simple")
    public ResponseEntity<Map<String, UserSimpleResponseDto>> getUserSimpleInfoData(@RequestParam List<String> userIds) {
        List<UserSimpleResponseDto> userSimpleResponseDtoList = userRepository.findUserSimpleInfoListByUserIds(userIds);
        Map<String, UserSimpleResponseDto> userData = new HashMap<>();

        for (UserSimpleResponseDto userSimpleResponseDto : userSimpleResponseDtoList) {
            if (!userData.containsKey(userSimpleResponseDto.getUserId())) {
                userData.put(userSimpleResponseDto.getUserId(), userSimpleResponseDto);
            }
        }

        return ResponseEntity.ok(userData);
    }

    @GetMapping("/basic")
    public ResponseEntity<Map<String, UserBasicResponseDto>> getUserBasicInfoData(@RequestParam List<String> userIds) {
        List<UserBasicResponseDto> userBasicResponseDtoList = userRepository.findUserBasicInfoListByUserIds(userIds);
        Map<String, UserBasicResponseDto> userData = new HashMap<>();

        for (UserBasicResponseDto userBasicResponseDto : userBasicResponseDtoList) {
            if (!userData.containsKey(userBasicResponseDto.getUserId())) {
                userData.put(userBasicResponseDto.getUserId(), userBasicResponseDto);
            }
        }

        return ResponseEntity.ok(userData);
    }
}