package com.soulside.service;

import com.soulside.dto.MeetingStartedRequest;
import com.soulside.dto.UserDTO;
import com.soulside.mapper.MeetingMapper;
import com.soulside.model.Meeting;
import com.soulside.model.MeetingSession;
import com.soulside.model.User;
import com.soulside.repository.MeetingRepository;
import com.soulside.repository.MeetingSessionRepository;
import com.soulside.repository.SessionParticipantRepository;
import com.soulside.repository.UserRepository;
import com.soulside.util.MeetingEventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class MeetingStartedHandler implements MeetingEventHandler<MeetingStartedRequest> {

    private static final Logger log = LoggerFactory.getLogger(MeetingStartedHandler.class);
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingSessionRepository meetingSessionRepository;
    private final SessionParticipantRepository sessionParticipantRepository;

    public MeetingStartedHandler(MeetingRepository meetingRepository,
                                 UserRepository userRepository,
                                 MeetingSessionRepository meetingSessionRepository,
                                 SessionParticipantRepository sessionParticipantRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.meetingSessionRepository = meetingSessionRepository;
        this.sessionParticipantRepository = sessionParticipantRepository;
    }

    @Override
    @Transactional
    public void handle(MeetingStartedRequest request) {
        log.info("[MEETING_EVENT] meetingId={}, sessionId={} - Handling meeting.started event", request.meeting().id(), request.meeting().sessionId());
        MeetingStartedRequest.MeetingDetail meetingDetail = request.meeting();
        User organizer = findOrCreateUser(meetingDetail.organizedBy());
        Meeting meeting = findOrCreateMeeting(meetingDetail, organizer);
        MeetingSession meetingSession = findOrCreateMeetingSession(meetingDetail.sessionId(), meeting, meetingDetail.startedAt());
        findOrCreateSessionParticipant(meetingSession, organizer);
        log.info("[MEETING_EVENT] meetingId={}, sessionId={} - Meeting persisted to the database", meeting.getMeetingId(), meetingSession.getSessionId());
    }

    private User findOrCreateUser(UserDTO userDto) {
        return userRepository
                .findByUserId(userDto.id())
                .orElseGet(() -> userRepository.save(MeetingMapper.toUserEntity(userDto)));
    }


    private Meeting findOrCreateMeeting(MeetingStartedRequest.MeetingDetail meetingDetail, User organizer) {
        return meetingRepository
                .findByMeetingId(meetingDetail.id())
                .orElseGet(() -> meetingRepository.save(MeetingMapper.toMeetingEntity(meetingDetail, organizer)));
    }


    private MeetingSession findOrCreateMeetingSession(String sessionId, Meeting meeting, Instant startedAt) {
        return meetingSessionRepository
                .findBySessionIdAndMeeting(sessionId, meeting)
                .orElseGet(() -> meetingSessionRepository.save(MeetingMapper.toMeetingSessionEntity(sessionId, meeting, startedAt)));
    }

    private void findOrCreateSessionParticipant(MeetingSession meetingSession, User participant) {
        sessionParticipantRepository
                .findBySessionAndUser(meetingSession, participant)
                .orElseGet(() -> sessionParticipantRepository.save(MeetingMapper.toSessionParticipantEntity(meetingSession, participant)));
    }

    @Override
    public String supportedEvent() {
        return MeetingEventConstants.MEETING_STARTED;
    }
}
