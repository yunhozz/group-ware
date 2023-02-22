package com.authserver.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> template;

    public void saveValue(String key, Object value) {
        ValueOperations<String, Object> ops = template.opsForValue();
        ops.set(key, value);
    }

    public void saveValue(String key, Object value, Duration duration) {
        ValueOperations<String, Object> ops = template.opsForValue();
        ops.set(key, value, duration);
    }

    public void saveData(String key, Object value) throws JsonProcessingException {
        ValueOperations<String, Object> ops = template.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(value);
        ops.set(key, json);
    }

    public Optional<Object> getValue(String key) {
        ValueOperations<String, Object> ops = template.opsForValue();
        return Optional.ofNullable(ops.get(key));
    }

    public <T> T getData(String key, Class<T> clazz) throws JsonProcessingException {
        ValueOperations<String, Object> ops = template.opsForValue();
        String json = (String) ops.get(key);

        if (StringUtils.hasText(json)) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, clazz);
        } else return null;
    }

    public void updateValue(String key, Object value) {
        ValueOperations<String, Object> ops = template.opsForValue();
        ops.getAndSet(key, value);
    }

    public void deleteValue(String key) {
        template.delete(key);
    }

    public String getMyInfoKey() {
        return "my-info";
    }
}