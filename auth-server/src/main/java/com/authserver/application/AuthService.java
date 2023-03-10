package com.authserver.application;

import com.authserver.application.exception.EmailDuplicateException;
import com.authserver.application.exception.EmailNotFoundException;
import com.authserver.application.exception.PasswordNotMatchException;
import com.authserver.application.exception.RefreshTokenDifferentException;
import com.authserver.application.exception.RefreshTokenNotFoundException;
import com.authserver.application.exception.UserNotFoundException;
import com.authserver.auth.jwt.JwtProvider;
import com.authserver.auth.session.UserPrincipal;
import com.authserver.common.enums.Provider;
import com.authserver.common.enums.Role;
import com.authserver.common.util.RandomIdUtils;
import com.authserver.common.util.RedisUtils;
import com.authserver.dto.request.JoinRequestDto;
import com.authserver.dto.request.LoginRequestDto;
import com.authserver.dto.response.TokenResponseDto;
import com.authserver.dto.response.UserBasicResponseDto;
import com.authserver.dto.response.UserProfileResponseDto;
import com.authserver.dto.response.UserResponseDto;
import com.authserver.dto.response.UserSimpleResponseDto;
import com.authserver.persistence.User;
import com.authserver.persistence.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import static com.authserver.common.util.RedisUtils.MY_INFO_KEY;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder encoder;
    private final RedisUtils redisUtils;

    @Transactional
    public String join(JoinRequestDto joinRequestDto) {
        validateEmailDuplicated(joinRequestDto);
        String userId = RandomIdUtils.generateUserId();
        User user = buildUser(joinRequestDto, userId);
        userRepository.save(user);

        return userId;
    }

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(EmailNotFoundException::new);
        UserResponseDto userResponseDto = new UserResponseDto(user);
        validatePasswordMatch(loginRequestDto, userResponseDto);

        TokenResponseDto tokenResponseDto = jwtProvider.createTokenDto(userResponseDto.getUserId(), userResponseDto.getRoles());
        saveAccessTokenOnResponse(response, tokenResponseDto);
        try {
            redisUtils.saveValue(userResponseDto.getUserId(), tokenResponseDto.getRefreshToken(), Duration.ofMillis(tokenResponseDto.getRefreshTokenValidTime()));
            redisUtils.saveData(MY_INFO_KEY, new UserSimpleResponseDto(userResponseDto.getUserId(), userResponseDto.getRoles()));
        } catch (Exception e) {
            throw new IllegalStateException(e.getLocalizedMessage());
        }

        return tokenResponseDto;
    }

    @Transactional(readOnly = true)
    public void logout(String token) {
        UserPrincipal userPrincipal = getUserPrincipal(token);
        redisUtils.deleteValue(userPrincipal.getUsername());
        redisUtils.deleteValue(MY_INFO_KEY);
        redisUtils.saveValue(token, "logout", Duration.ofMinutes(10)); // 10 ?????? ???????????? ?????? ??????
    }

    @Transactional(readOnly = true)
    public TokenResponseDto tokenReissue(String refreshToken, HttpServletResponse response) {
        UserPrincipal userPrincipal = getUserPrincipal(refreshToken);
        Optional<String> redisToken = redisUtils.getValue(userPrincipal.getUsername());

        validateRefreshToken(refreshToken, redisToken);

        TokenResponseDto tokenResponseDto = jwtProvider.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRoles());
        saveAccessTokenOnResponse(response, tokenResponseDto);
        redisUtils.updateValue(userPrincipal.getUsername(), tokenResponseDto.getRefreshToken());

        return tokenResponseDto;
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto findUserProfileByToken(String token) {
        UserPrincipal userPrincipal = getUserPrincipal(token);
        return new UserProfileResponseDto(userPrincipal.getUser());
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto findUserProfileByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
        return new UserProfileResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserSimpleResponseDto findUserSimpleInfoByUserId(String userId) {
        return userRepository.findUserSimpleInfoByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public UserBasicResponseDto findUserBasicInfoByUserId(String userId) {
        return userRepository.findUserBasicInfoByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private User buildUser(JoinRequestDto joinRequestDto, String userId) {
        return User.builder()
                .userId(userId)
                .email(joinRequestDto.getEmail())
                .password(encoder.encode(joinRequestDto.getPassword()))
                .name(joinRequestDto.getName())
                .imageUrl(joinRequestDto.getImageUrl())
                .provider(Provider.LOCAL)
                .roles(Set.of(Role.GUEST))
                .build();
    }

    private void saveAccessTokenOnResponse(HttpServletResponse response, TokenResponseDto tokenResponseDto) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader(HttpHeaders.AUTHORIZATION, tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
    }

    private UserPrincipal getUserPrincipal(String token) {
        Authentication authentication = jwtProvider.getAuthentication(token.split(" ")[1]);
        return (UserPrincipal) authentication.getPrincipal();
    }

    private void validateEmailDuplicated(JoinRequestDto joinRequestDto) {
        if (userRepository.existsUserByEmail(joinRequestDto.getEmail())) {
            throw new EmailDuplicateException();
        }
    }

    private void validatePasswordMatch(LoginRequestDto loginRequestDto, UserResponseDto userResponseDto) {
        if (!encoder.matches(loginRequestDto.getPassword(), userResponseDto.getPassword())) {
            throw new PasswordNotMatchException();
        }
    }

    private void validateRefreshToken(String refreshToken, Optional<String> redisToken) {
        if (redisToken.isEmpty()) {
            throw new RefreshTokenNotFoundException();
        }

        if (!redisToken.get().equals(refreshToken.split(" ")[1])) {
            throw new RefreshTokenDifferentException();
        }
    }
}