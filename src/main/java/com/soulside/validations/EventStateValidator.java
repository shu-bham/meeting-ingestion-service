package com.soulside.validations;

import com.soulside.dto.MeetingEndedRequest;
import com.soulside.dto.MeetingEventRequest;
import com.soulside.dto.MeetingTranscriptRequest;
import com.soulside.exception.InvalidEventOrderException;
import com.soulside.model.MeetingSession;
import com.soulside.model.MeetingSessionStatus;
import com.soulside.repository.MeetingRepository;
import com.soulside.repository.MeetingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventStateValidator {

    private static final Logger log = LoggerFactory.getLogger(EventStateValidator.class);
    private final MeetingRepository meetingRepository;
    private final MeetingSessionRepository meetingSessionRepository;

    public EventStateValidator(MeetingRepository meetingRepository,
                               MeetingSessionRepository meetingSessionRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingSessionRepository = meetingSessionRepository;
    }

    public void validate(MeetingEventRequest request) {
        if (request instanceof MeetingTranscriptRequest transcriptRequest) {
            validateTranscript(transcriptRequest);
        } else if (request instanceof MeetingEndedRequest endedRequest) {
            validateEnded(endedRequest);
        }
    }

    private void validateTranscript(MeetingTranscriptRequest request) {
        String meetingId = request.meeting().id();
        String sessionId = request.meeting().sessionId();

        MeetingSession session = findSession(meetingId, sessionId)
                .orElseThrow(() -> new InvalidEventOrderException(String.format("Cannot process transcript: Meeting session not found for meetingId: %s, sessionId: %s. 'meeting.started' event may be missing.", meetingId, sessionId)));

        if (session.getStatus() == MeetingSessionStatus.ENDED) {
            throw new InvalidEventOrderException(String.format("Cannot process transcript: Meeting session %s has already ended.", sessionId));
        }
    }

    private void validateEnded(MeetingEndedRequest request) {
        String meetingId = request.meeting().id();
        String sessionId = request.meeting().sessionId();

        findSession(meetingId, sessionId)
                .orElseThrow(() -> new InvalidEventOrderException(String.format("Cannot process ended event: Meeting session not found for meetingId: %s, sessionId: %s.", meetingId, sessionId)));
    }

    private Optional<MeetingSession> findSession(String meetingId, String sessionId) {
        return meetingRepository
                .findByMeetingId(meetingId)
                .flatMap(meeting -> meetingSessionRepository.findBySessionIdAndMeeting(sessionId, meeting));
    }
}
