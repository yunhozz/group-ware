package com.authserver.auth.jwt;

import com.authserver.auth.session.UserDetailsServiceImpl;
import com.authserver.common.enums.Role;
import com.authserver.dto.response.TokenResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.grantType}")
    private String grantType;

    @Value("${app.jwt.accessTime}")
    private Long accessTokenValidTime;

    @Value("${app.jwt.refreshTime}")
    private Long refreshTokenValidTime;

    private final UserDetailsServiceImpl userDetailsService;

    public TokenResponseDto createTokenDto(String userId, Role role) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("auth", role.getAuthority());

        String accessToken = createToken(claims, JwtType.ACCESS_TOKEN_TYPE, accessTokenValidTime);
        String refreshToken = createToken(claims, JwtType.REFRESH_TOKEN_TYPE, refreshTokenValidTime);

        return TokenResponseDto.builder()
                .grantType(grantType)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenValidTime(refreshTokenValidTime)
                .build();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean isValidateToken(String token) {
        try {
            parseToken(token);
            return true;

        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 Jwt 서명입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다.");
        }

        return false;
    }

    private String createToken(Claims claims, String type, Long validTime) {
        claims.put("type", type);
        return Jwts.builder()
                .setClaims(claims)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + validTime))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}