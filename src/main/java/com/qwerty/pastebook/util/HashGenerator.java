package com.qwerty.pastebook.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class HashGenerator {

    private final AtomicLong counter = new AtomicLong();

    public String generate() {
        return Long.toHexString(counter.getAndIncrement());
    }
}
