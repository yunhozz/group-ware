package com.apigateway.filter.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenExceptionHandler implements ErrorWebExceptionHandler {

    // 필터에서 throw 한 exception 을 잡아서 응답

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        byte[] bytes = ex.getMessage().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);

        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}