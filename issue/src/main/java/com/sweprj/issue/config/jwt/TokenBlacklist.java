package com.sweprj.issue.config.jwt;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {
    private final Map<String, Boolean> blacklist = new ConcurrentHashMap<>();

    public void add(String token) {
        blacklist.put(token, true);
    }

    public boolean contains(String token) {
        return blacklist.containsKey(token);
    }
}
