package com.authserver.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> template;

    public void saveValue(String key, Object value, Duration duration) {
        ValueOperations<String, Object> ops = template.opsForValue();
        ops.set(key, value, duration);
    }

    public Optional<Object> getValue(String key) {
        ValueOperations<String, Object> ops = template.opsForValue();
        return Optional.ofNullable(ops.get(key));
    }

    public void updateValue(String key, Object value) {
        ValueOperations<String, Object> ops = template.opsForValue();
        ops.getAndSet(key, value);
    }

    public void deleteValue(String key) {
        template.delete(key);
    }
}