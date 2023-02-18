package com.apigateway.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Strings;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenResolver {

    @Value("${app.jwt.secret}")
    private String secretKey;

    public Optional<String> resolve(String token) {
        return resolveToken(token);
    }

    public String getAuth(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("auth");
    }

    public long getRemainingTime(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime() - new Date().getTime();
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Optional<String> resolveToken(String token) {
        return Strings.hasText(token) ? resolveParts(token) : Optional.empty();
    }

    private Optional<String> resolveParts(String token) {
        String[] parts = token.split(" ");
        return parts.length == 2 && parts[0].equals("Bearer") ? Optional.ofNullable(parts[1]) : Optional.empty();
    }

    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}