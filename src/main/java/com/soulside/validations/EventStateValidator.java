package com.soulside.validations;

import com.soulside.dto.MeetingEndedRequest;
import com.soulside.dto.MeetingEventRequest;
import com.soulside.dto.MeetingStartedRequest;
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
        if (request instanceof MeetingStartedRequest startedRequest) {
            validateStarted(startedRequest);
        } else if (request instanceof MeetingTranscriptRequest transcriptRequest) {
            validateTranscript(transcriptRequest);
        } else if (request instanceof MeetingEndedRequest endedRequest) {
            validateEnded(endedRequest);
        }
    }

    private void validateStarted(com.soulside.dto.MeetingStartedRequest request) {
        if (isSessionEnded(request.getKey(), request.meeting().id(), request.meeting().sessionId())) {
            throw new InvalidEventOrderException(String.format("Cannot process start event: Meeting session %s has already ended.", request.meeting().sessionId()));
        }
    }

    private void validateTranscript(MeetingTranscriptRequest request) {
        if (isSessionEnded(request.getKey(), request.meeting().id(), request.meeting().sessionId())) {
            throw new InvalidEventOrderException(String.format("Cannot process transcript: Meeting session %s has already ended.", request.meeting().sessionId()));
        }
    }

    private void validateEnded(MeetingEndedRequest request) {
        Object cachedStatus = cacheService.getCachedStatus(request.getKey());
        if (cachedStatus != null) {
            if (!MeetingSessionStatus.IN_PROGRESS.name().equals(cachedStatus.toString())) {
                throw new InvalidEventOrderException(String.format("Cannot end a meeting session that is not in progress. Current status: %s", cachedStatus));
            }
        } else {
            var session = findSession(request.meeting().id(), request.meeting().sessionId())
                    .orElseThrow(() -> new InvalidEventOrderException(String.format("Cannot process ended event: Meeting session not found for meetingId: %s, sessionId: %s.", request.meeting().id(), request.meeting().sessionId())));

            if (session.getStatus() != MeetingSessionStatus.IN_PROGRESS) {
                throw new InvalidEventOrderException(String.format("Cannot end a meeting session that is not in progress. Current status: %s", session.getStatus()));
            }
        }
    }

    public boolean isSessionEnded(String cacheKey, String meetingId, String sessionId) {
        Object cachedStatus = cacheService.getCachedStatus(cacheKey);
        if (cachedStatus != null) {
            return MeetingSessionStatus.ENDED.name().equals(cachedStatus.toString());
        }
        return findSession(meetingId, sessionId)
                .map(session -> session.getStatus() == MeetingSessionStatus.ENDED)
                .orElse(false);
    }

    public Optional<MeetingSession> findSession(String meetingId, String sessionId) {
        return meetingRepository
                .findByMeetingId(meetingId)
                .flatMap(meeting -> meetingSessionRepository.findBySessionIdAndMeeting(sessionId, meeting));
    }
}
