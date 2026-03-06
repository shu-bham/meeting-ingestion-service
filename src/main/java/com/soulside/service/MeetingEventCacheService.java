package com.soulside.service;

import com.soulside.dto.MeetingEventRequest;
import com.soulside.model.MeetingSessionStatus;
import com.soulside.util.MeetingEventConstants;
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
        String cacheKey = getEventStatusCacheKey(request.getKey());
        switch (request.event()) {
            case MeetingEventConstants.MEETING_STARTED -> {
                logger.info("[MEETING_EVENT_CACHE] key: {}, Meeting started, Caching status STARTED.", request.getKey());
                redisService.setWithExpiration(cacheKey, MeetingSessionStatus.STARTED.name(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            }
            case MeetingEventConstants.MEETING_TRANSCRIPT ->
                    redisService.setWithExpiration(cacheKey, MeetingSessionStatus.IN_PROGRESS.name(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            case MeetingEventConstants.MEETING_ENDED -> {
                logger.info("[MEETING_EVENT_CACHE] key: {}, Meeting ended, Caching status ENDED.", request.getKey());
                redisService.setWithExpiration(cacheKey, MeetingSessionStatus.ENDED.name(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            }
        }
    }

    public Object getCachedStatus(String key) {
        Object status = redisService.get(getEventStatusCacheKey(key));
        if (status == null) {
            logger.info("[MEETING_EVENT_CACHE] key={} - Cache miss.", key);
        }
        return status;
    }

    private String getEventStatusCacheKey(String key) {
        return "meeting:status:" + key;
    }
}
