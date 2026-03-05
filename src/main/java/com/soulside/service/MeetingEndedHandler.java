package com.soulside.service;

import com.soulside.dto.MeetingEndedRequest;
import com.soulside.exception.MeetingNotFoundException;
import com.soulside.model.Meeting;
import com.soulside.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeetingEndedHandler implements MeetingEventHandler<MeetingEndedRequest> {

    private static final Logger log = LoggerFactory.getLogger(MeetingEndedHandler.class);
    private final MeetingRepository meetingRepository;

    public MeetingEndedHandler(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    @Transactional
    public void handle(MeetingEndedRequest request) {
        log.info("Handling meeting.ended event for meetingId: {}", request.meeting().id());
        Meeting meeting = findMeeting(request.meeting().id());
        meeting.setStatus(request.meeting().status());
        meeting.setEndedAt(request.meeting().endedAt());
        meetingRepository.save(meeting);
        log.info("Meeting with meetingId: {} updated to ended status", meeting.getMeetingId());
    }

    private Meeting findMeeting(String meetingId) {
        return meetingRepository
                .findByMeetingId(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException("No meeting found with meetingId: " + meetingId));
    }

    @Override
    public String supportedEvent() {
        return "meeting.ended";
    }
}
