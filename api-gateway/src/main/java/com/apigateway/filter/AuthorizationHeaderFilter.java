package com.apigateway.filter;

import com.apigateway.common.util.TokenResolver;
import com.apigateway.filter.exception.LogoutTokenException;
import com.apigateway.filter.exception.TokenFormatInvalidException;
import com.apigateway.filter.exception.TokenNearExpirationException;
import com.apigateway.filter.exception.TokenNotFoundException;
import com.apigateway.filter.exception.TokenParsingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final TokenResolver tokenResolver;
    private final RedisTemplate<String, Object> template;

    public AuthorizationHeaderFilter(TokenResolver tokenResolver, RedisTemplate<String, Object> template) {
        super(Config.class);
        this.tokenResolver = tokenResolver;
        this.template = template;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            List<String> tokens = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            log.info("[Request URI] " + request.getURI());

            if (tokens == null || tokens.isEmpty()) {
                throw new TokenNotFoundException();
            }

            String headerToken = tokens.get(0);
            Optional<String> optionalToken = tokenResolver.resolve(headerToken);

            // 토큰이 존재하나 형식이 잘못되었을 때
            if (optionalToken.isEmpty()) {
                throw new TokenFormatInvalidException();
            }

            try {
                String token = optionalToken.get();
                // redis 에 존재하는 로그아웃 토큰으로 요청했을 때
                if (getValueFromRedis(token).isPresent()) {
                    throw new LogoutTokenException();
                }

                // 토큰의 만료 시간이 5분 이내로 남았을 때
                if (tokenResolver.getRemainingTime(token) < 300000) {
                    throw new TokenNearExpirationException();
                }

            } catch (Exception e) {
                throw new TokenParsingException(e.getLocalizedMessage());
            }

            return chain.filter(exchange);
        }, -1);
    }

    private Optional<Object> getValueFromRedis(String key) {
        ValueOperations<String, Object> ops = template.opsForValue();
        return Optional.ofNullable(ops.get(key));
    }

    public static class Config {

    }
}