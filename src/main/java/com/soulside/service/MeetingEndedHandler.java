package com.soulside.service;

import com.soulside.dto.MeetingEndedRequest;
import com.soulside.exception.MeetingNotFoundException;
import com.soulside.exception.MeetingSessionNotFoundException;
import com.soulside.model.Meeting;
import com.soulside.model.MeetingSession;
import com.soulside.model.MeetingSessionStatus;
import com.soulside.repository.MeetingRepository;
import com.soulside.repository.MeetingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeetingEndedHandler implements MeetingEventHandler<MeetingEndedRequest> {

    private static final Logger log = LoggerFactory.getLogger(MeetingEndedHandler.class);
    private final MeetingRepository meetingRepository;
    private final MeetingSessionRepository meetingSessionRepository;

    public MeetingEndedHandler(MeetingRepository meetingRepository, MeetingSessionRepository meetingSessionRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingSessionRepository = meetingSessionRepository;
    }

    @Override
    @Transactional
    public void handle(MeetingEndedRequest request) {
        log.info("Handling meeting.ended event for meetingId: {}, sessionId: {}", request.meeting().id(), request.meeting().sessionId());
        Meeting meeting = findMeeting(request.meeting().id());
        MeetingSession meetingSession = findMeetingSession(request.meeting().sessionId(), meeting);
        meetingSession.setStatus(MeetingSessionStatus.ENDED);
        meetingSession.setEndedAt(request.meeting().endedAt());
        meetingSessionRepository.save(meetingSession);
        log.info("Meeting with meetingId: {}, sessionId: {} updated to ended status", meeting.getMeetingId(), meetingSession.getSessionId());
    }

    private Meeting findMeeting(String meetingId) {
        return meetingRepository
                .findByMeetingId(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException("No meeting found with meetingId: " + meetingId));
    }

    private MeetingSession findMeetingSession(String sessionId, Meeting meeting) {
        return meetingSessionRepository
                .findBySessionIdAndMeeting(sessionId, meeting)
                .orElseThrow(() -> new MeetingSessionNotFoundException("No meeting session found with sessionId: " + sessionId));
    }

    @Override
    public String supportedEvent() {
        return "meeting.ended";
    }
}
