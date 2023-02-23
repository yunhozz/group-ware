package com.postservice.common.util;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomIdUtils {

    private static final int counterMax = 256 * 256;
    private static final AtomicInteger intVal = new AtomicInteger(0);

    public static String generateFileId() {
        return "F" + generate();
    }

    private static String generate() {
        long epochMilli = Instant.now().toEpochMilli();
        int randomInteger = intVal.accumulateAndGet(1, (index, inc) -> (index + inc) % counterMax);
        final long id = epochMilli * counterMax + randomInteger;

        return Long.toHexString(id);
    }

    private RandomIdUtils() {
        super();
    }
}