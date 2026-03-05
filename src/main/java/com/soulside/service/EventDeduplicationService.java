package com.soulside.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EventDeduplicationService {

    private final RedisService redisService;
    private final Long DEDUP_TIMEOUT = 300L;

    public EventDeduplicationService(RedisService redisService) {
        this.redisService = redisService;
    }

    public boolean isDuplicate(String eventHash) {
        return redisService.get(eventHash) != null;
    }

    public void storeEventHash(String eventHash) {
        redisService.setWithExpiration(eventHash, "true", DEDUP_TIMEOUT, TimeUnit.SECONDS);
    }
}
