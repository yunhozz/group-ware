package com.apigateway.filter;

import com.apigateway.common.util.TokenResolver;
import com.apigateway.filter.exception.HaveNotAuthorityException;
import com.apigateway.filter.exception.TokenParsingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AuthorizationGuestFilter extends AbstractGatewayFilterFactory<AuthorizationGuestFilter.Config> {

    private final TokenResolver tokenResolver;

    public AuthorizationGuestFilter(TokenResolver tokenResolver) {
        super(Config.class);
        this.tokenResolver = tokenResolver;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            List<String> tokens = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            log.info("[CHECK GUEST]");

            String headerToken = tokens.get(0);
            tokenResolver.resolve(headerToken).ifPresent(token -> {
                try {
                    String auth = tokenResolver.getAuth(token);
                    if (!auth.contains("GUEST")) {
                        log.error("[ACCESS TOKEN IS NOT AUTHORIZED]");
                        throw new HaveNotAuthorityException(auth);
                    }

                    log.info("[ACCESS TOKEN IS OK]");

                } catch (Exception e) {
                    throw new TokenParsingException(e.getLocalizedMessage());
                }
            });

            return chain.filter(exchange);
        };
    }

    public static class Config {

    }
}