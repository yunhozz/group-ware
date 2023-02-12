package com.apigateway.filter;

import com.apigateway.filter.exception.LogoutTokenException;
import com.apigateway.filter.exception.TokenParsingException;
import com.apigateway.filter.exception.TokenNearExpirationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Strings;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${app.jwt.secret}")
    private String secretKey;

    private final RedisTemplate<String, Object> template;

    public AuthorizationHeaderFilter(RedisTemplate<String, Object> template) {
        super(Config.class);
        this.template = template;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            List<String> tokens = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            log.info("[Request URI] " + request.getURI());

            if (tokens == null || tokens.isEmpty()) {
                log.info("[TOKEN-FREE SERVICE]");
                return chain.filter(exchange);
            }

            String headerToken = tokens.get(0);
            resolveToken(headerToken).ifPresent(token -> {
                try {
                    Jwts.parserBuilder()
                            .setSigningKey(getSecretKey())
                            .build()
                            .parseClaimsJws(token);

                    // redis 에 존재하는 로그아웃 토큰으로 요청했을 때
                    if (getValueFromRedis(token).isPresent()) {
                        throw new LogoutTokenException();
                    }
                    // 토큰의 만료 시간이 5분 이내로 남았을 때
                    if (getRemainingTime(token) < 300000) {
                        throw new TokenNearExpirationException();
                    }
                    // grant type 을 제외한 토큰 요청으로 변경
                    log.info("[ACCESS TOKEN IS OK]");
                    request.mutate()
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .build();

                } catch (Exception e) {
                    throw new TokenParsingException(e.getLocalizedMessage());
                }
            });

            return chain.filter(exchange);
        };
    }

    private Optional<String> resolveToken(String token) {
        return Strings.hasText(token) ? resolveParts(token) : Optional.empty();
    }

    private Optional<String> resolveParts(String token) {
        String[] parts = token.split(" ");
        return parts.length == 2 && parts[0].equals("Bearer") ? Optional.ofNullable(parts[1]) : Optional.empty();
    }

    private Optional<Object> getValueFromRedis(String key) {
        ValueOperations<String, Object> ops = template.opsForValue();
        return Optional.ofNullable(ops.get(key));
    }

    private Long getRemainingTime(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().getTime() - new Date().getTime();
    }

    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static class Config {

    }
}