package com.authserver.application;

import com.authserver.application.exception.EmailDuplicateException;
import com.authserver.application.exception.EmailNotFoundException;
import com.authserver.application.exception.PasswordNotMatchException;
import com.authserver.application.exception.RefreshTokenDifferentException;
import com.authserver.application.exception.RefreshTokenNotFoundException;
import com.authserver.auth.jwt.JwtProvider;
import com.authserver.auth.session.UserPrincipal;
import com.authserver.common.enums.Provider;
import com.authserver.common.enums.Role;
import com.authserver.common.util.RandomIdUtils;
import com.authserver.common.util.RedisUtils;
import com.authserver.dto.request.JoinRequestDto;
import com.authserver.dto.request.LoginRequestDto;
import com.authserver.dto.response.TokenResponseDto;
import com.authserver.persistence.User;
import com.authserver.persistence.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    BCryptPasswordEncoder encoder;

    @Mock
    RedisUtils redisUtils;

    @Mock
    RandomIdUtils randomIdUtils;

    @Mock
    HttpServletResponse response;

    User user;

    @BeforeEach
    void beforeEach() {
        user = createUser();
    }

    @Test
    @DisplayName("회원가입")
    void join() throws Exception {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto("test@gmail.com", "123", "tester", "test.png");
        given(userRepository.existsUserByEmail(anyString())).willReturn(false);
        given(randomIdUtils.generateUserId()).willReturn("userId");

        // when
        String result = authService.join(joinRequestDto);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result).isEqualTo("userId");
    }

    @Test
    @DisplayName("회원가입 시 이메일 중복")
    void joinThrowsEmailDuplicate() throws Exception {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto("test@gmail.com", "123", "tester", "test.png");
        given(userRepository.existsUserByEmail(anyString())).willReturn(true);

        // then
        assertThrows(EmailDuplicateException.class, () -> authService.join(joinRequestDto));
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        // given
        TokenResponseDto tokenResponseDto = new TokenResponseDto("test", "atk", "rtk", 100L);
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@gmail.com", "123");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(encoder.matches(loginRequestDto.getPassword(), user.getPassword())).willReturn(true);
        given(jwtProvider.createTokenDto(anyString(), anySet())).willReturn(tokenResponseDto);
        willDoNothing().given(redisUtils).saveValue(user.getUserId(), tokenResponseDto.getRefreshToken(), Duration.ofMillis(tokenResponseDto.getRefreshTokenValidTime()));

        // when
        TokenResponseDto result = authService.login(loginRequestDto, response);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result.getGrantType()).isEqualTo("test");
        assertThat(result.getAccessToken()).isEqualTo("atk");
        assertThat(result.getRefreshToken()).isEqualTo("rtk");
    }

    @Test
    @DisplayName("로그인 시 이메일 조회 실패")
    void loginThrowsEmailNotFoundException() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@gmail.com", "123");
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // then
        assertThrows(EmailNotFoundException.class, () -> authService.login(loginRequestDto, response));
    }

    @Test
    @DisplayName("로그인 시 비밀번호 불일치")
    void loginThrowsPasswordNotMatchException() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@gmail.com", "123");
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(encoder.matches(loginRequestDto.getPassword(), user.getPassword())).willReturn(false);

        // then
        assertThrows(PasswordNotMatchException.class, () -> authService.login(loginRequestDto, response));
    }

    @Test
    @DisplayName("jwt 토큰 재발급")
    void tokenReissue() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String refreshToken = "rtk";
        String redisToken = "rtk";
        TokenResponseDto tokenResponseDto = new TokenResponseDto("test", "atk", "rtk", 100L);

        given(jwtProvider.getAuthentication(anyString())).willReturn(new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities()));
        given(redisUtils.getValue(anyString())).willReturn(Optional.of(redisToken));
        given(jwtProvider.createTokenDto(anyString(), anySet())).willReturn(tokenResponseDto);
        willDoNothing().given(redisUtils).updateValue(userPrincipal.getUsername(), tokenResponseDto.getRefreshToken());

        // when
        TokenResponseDto result = authService.tokenReissue("test " + refreshToken, response);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result.getGrantType()).isEqualTo("test");
        assertThat(result.getAccessToken()).isEqualTo("atk");
        assertThat(result.getRefreshToken()).isEqualTo("rtk");
    }

    @Test
    @DisplayName("jwt 토큰 재발급 시 redis 토큰이 존재하지 않음")
    void tokenReissueThrowsRefreshTokenNotFoundException() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String refreshToken = "rtk";

        given(jwtProvider.getAuthentication(anyString())).willReturn(new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities()));
        given(redisUtils.getValue(anyString())).willReturn(Optional.empty());

        // then
        assertThrows(RefreshTokenNotFoundException.class, () -> authService.tokenReissue("test " + refreshToken, response));
    }

    @Test
    @DisplayName("jwt 토큰 재발급 시 redis 토큰이랑 일치하지 않음")
    void tokenReissueThrowsRefreshTokenDifferentException() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String refreshToken = "rtk";
        String redisToken = "wrong-token";

        given(jwtProvider.getAuthentication(anyString())).willReturn(new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities()));
        given(redisUtils.getValue(anyString())).willReturn(Optional.of(redisToken));

        // then
        assertThrows(RefreshTokenDifferentException.class, () -> authService.tokenReissue("test " + refreshToken, response));
    }

    private User createUser() {
        return User.builder()
                .userId("userId")
                .email("test@gmail.com")
                .password(encoder.encode("123"))
                .name("tester")
                .imageUrl("test.png")
                .provider(Provider.LOCAL)
                .roles(Set.of(Role.GUEST, Role.USER))
                .build();
    }
}