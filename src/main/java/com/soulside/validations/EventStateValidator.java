package com.soulside.validations;

import com.soulside.dto.MeetingEndedRequest;
import com.soulside.dto.MeetingEventRequest;
import com.soulside.dto.MeetingTranscriptRequest;
import com.soulside.exception.InvalidEventOrderException;
import com.soulside.model.MeetingSession;
import com.soulside.model.MeetingSessionStatus;
import com.soulside.repository.MeetingRepository;
import com.soulside.repository.MeetingSessionRepository;
import com.soulside.service.MeetingEventCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventStateValidator {

    private static final Logger log = LoggerFactory.getLogger(EventStateValidator.class);
    private final MeetingRepository meetingRepository;
    private final MeetingSessionRepository meetingSessionRepository;
    private final MeetingEventCacheService cacheService;

    public EventStateValidator(MeetingRepository meetingRepository,
                               MeetingSessionRepository meetingSessionRepository,
                               MeetingEventCacheService cacheService) {
        this.meetingRepository = meetingRepository;
        this.meetingSessionRepository = meetingSessionRepository;
        this.cacheService = cacheService;
    }

    public void validate(MeetingEventRequest request) {
        if (request instanceof MeetingTranscriptRequest transcriptRequest) {
            validateTranscript(transcriptRequest);
        } else if (request instanceof MeetingEndedRequest endedRequest) {
            validateEnded(endedRequest);
        }
    }

    private void validateTranscript(MeetingTranscriptRequest request) {
        if (isSessionEnded(request.getKey(), request.meeting().id(), request.meeting().sessionId())) {
            throw new InvalidEventOrderException(String.format("Cannot process transcript: Meeting session %s has already ended.", request.meeting().sessionId()));
        }
    }

    private void validateEnded(MeetingEndedRequest request) {
        Object cachedStatus = cacheService.getCachedStatus(request.getKey());
        if (cachedStatus == null) {
            findSession(request.meeting().id(), request.meeting().sessionId())
                    .orElseThrow(() -> new InvalidEventOrderException(String.format("Cannot process ended event: Meeting session not found for meetingId: %s, sessionId: %s.", request.meeting().id(), request.meeting().sessionId())));
        }
    }

    private boolean isSessionEnded(String cacheKey, String meetingId, String sessionId) {
        Object cachedStatus = cacheService.getCachedStatus(cacheKey);
        if (cachedStatus != null) {
            return MeetingSessionStatus.ENDED.name().equals(cachedStatus.toString());
        }
        return findSession(meetingId, sessionId)
                .map(session -> session.getStatus() == MeetingSessionStatus.ENDED)
                .orElse(false);
    }

    private Optional<MeetingSession> findSession(String meetingId, String sessionId) {
        return meetingRepository
                .findByMeetingId(meetingId)
                .flatMap(meeting -> meetingSessionRepository.findBySessionIdAndMeeting(sessionId, meeting));
    }
}
