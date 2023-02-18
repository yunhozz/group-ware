package com.postservice.common.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RandomIdUtils implements RandomIdGenerator {

    private static final int counterMax = 256 * 256;
    private static final AtomicInteger intVal = new AtomicInteger(0);

    @Override
    public String generateFileId() {
        return "F" + generate();
    }

    private String generate() {
        long epochMilli = Instant.now().toEpochMilli();
        int randomInteger = intVal.accumulateAndGet(1, (index, inc) -> (index + inc) % counterMax);
        final long id = epochMilli * counterMax + randomInteger;

        return Long.toHexString(id);
    }
}