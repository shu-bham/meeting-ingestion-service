package com.soulside.service;

import com.soulside.dto.MeetingEventRequest;
import com.soulside.model.MeetingSessionStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MeetingEventCacheService {

    private final RedisService redisService;
    private static final long CACHE_TTL_HOURS = 6;


    public MeetingEventCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void updateCache(MeetingEventRequest request) {
        String cacheKey = getCacheKey(request.getKey());
        switch (request.event()) {
            case "meeting.started" ->
                    redisService.setWithExpiration(cacheKey, MeetingSessionStatus.STARTED.name(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            case "meeting.transcript" ->
                    redisService.setWithExpiration(cacheKey, MeetingSessionStatus.IN_PROGRESS.name(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            case "meeting.ended" -> redisService.delete(cacheKey);
        }
    }

    public Object getCachedStatus(String key) {
        return redisService.get(getCacheKey(key));
    }

    private String getCacheKey(String key) {
        return "meeting:status:" + key;
    }
}
