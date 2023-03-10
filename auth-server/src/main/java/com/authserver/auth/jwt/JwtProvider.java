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
import java.util.Set;
import java.util.stream.Collectors;

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

    public TokenResponseDto createTokenDto(String userId, Set<Role> roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        String auth = roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("auth", auth);

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
            log.error("????????? ???????????????.");
        } catch (SecurityException | MalformedJwtException e) {
            log.error("????????? Jwt ???????????????.");
        } catch (UnsupportedJwtException e) {
            log.error("???????????? ?????? ???????????????.");
        } catch (IllegalArgumentException e) {
            log.error("????????? ???????????????.");
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