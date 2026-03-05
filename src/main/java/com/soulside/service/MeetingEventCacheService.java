package com.soulside.service;

import com.soulside.dto.MeetingEventRequest;
import com.soulside.model.MeetingSessionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MeetingEventCacheService {

    private final RedisService redisService;
    private static final long CACHE_TTL_HOURS = 6;
    private static final Logger logger = LoggerFactory.getLogger(MeetingEventCacheService.class);


    public MeetingEventCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void updateCache(MeetingEventRequest request) {
        String cacheKey = getCacheKey(request.getKey());
        switch (request.event()) {
            case "meeting.started" -> {
                logger.info("Meeting started for key: {}. Caching status STARTED.", request.getKey());
                redisService.setWithExpiration(cacheKey, MeetingSessionStatus.STARTED.name(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            }
            case "meeting.transcript" ->
                    redisService.setWithExpiration(cacheKey, MeetingSessionStatus.IN_PROGRESS.name(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            case "meeting.ended" -> {
                logger.info("Meeting ended for key: {}. Deleting cache.", request.getKey());
                redisService.delete(cacheKey);
            }
        }
    }

    public Object getCachedStatus(String key) {
        Object status = redisService.get(getCacheKey(key));
        if (status == null) {
            logger.info("Cache miss for key: {}.", key);
        }
        return status;
    }

    private String getCacheKey(String key) {
        return "meeting:status:" + key;
    }
}
