package com.soulside.service;

import com.soulside.dto.MeetingTranscriptRequest;
import com.soulside.dto.UserDTO;
import com.soulside.exception.MeetingNotFoundException;
import com.soulside.exception.MeetingSessionNotFoundException;
import com.soulside.mapper.MeetingMapper;
import com.soulside.model.Meeting;
import com.soulside.model.MeetingSession;
import com.soulside.model.SessionParticipant;
import com.soulside.model.User;
import com.soulside.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.soulside.mapper.MeetingMapper.toTranscriptEntity;

@Service
public class MeetingTranscriptHandler implements MeetingEventHandler<MeetingTranscriptRequest> {

    private static final Logger log = LoggerFactory.getLogger(MeetingTranscriptHandler.class);
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingSessionRepository meetingSessionRepository;
    private final TranscriptRepository transcriptRepository;
    private final SessionParticipantRepository sessionParticipantRepository;


    public MeetingTranscriptHandler(MeetingRepository meetingRepository,
                                    UserRepository userRepository,
                                    MeetingSessionRepository meetingSessionRepository,
                                    TranscriptRepository transcriptRepository,
                                    SessionParticipantRepository sessionParticipantRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.meetingSessionRepository = meetingSessionRepository;
        this.transcriptRepository = transcriptRepository;
        this.sessionParticipantRepository = sessionParticipantRepository;
    }

    @Override
    @Transactional
    public void handle(MeetingTranscriptRequest request) {
        log.info("Handling meeting.transcript event for meetingId: {}, sessionId: {}", request.meeting().id(), request.meeting().sessionId());
        Meeting meeting = findMeeting(request.meeting().id());
        MeetingSession meetingSession = findMeetingSession(request.meeting().sessionId(), meeting);
        User participant = findOrCreateUser(request.data().speaker());
        SessionParticipant sessionParticipant = findOrCreateSessionParticipant(meetingSession, participant);
        transcriptRepository.save(toTranscriptEntity(request, sessionParticipant));
        log.info("Meeting transcript for meetingId: {}, sessionId: {} persisted to the database", meeting.getMeetingId(), meetingSession.getSessionId());
    }

    private SessionParticipant findOrCreateSessionParticipant(MeetingSession meetingSession, User participant) {
        return sessionParticipantRepository
                .findBySessionAndUser(meetingSession, participant)
                .orElseGet(() -> sessionParticipantRepository.save(MeetingMapper.toSessionParticipantEntity(meetingSession, participant)));
    }


    private User findOrCreateUser(UserDTO userDto) {
        return userRepository
                .findByUserId(userDto.id())
                .orElseGet(() -> userRepository.save(MeetingMapper.toUserEntity(userDto)));
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
        return "meeting.transcript";
    }
}
