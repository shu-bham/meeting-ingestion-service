package com.soulside.service;

import com.soulside.dto.MeetingStartedRequest;
import com.soulside.dto.UserDTO;
import com.soulside.mapper.MeetingMapper;
import com.soulside.model.Meeting;
import com.soulside.model.User;
import com.soulside.repository.MeetingRepository;
import com.soulside.repository.MeetingSessionRepository;
import com.soulside.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeetingStartedHandler implements MeetingEventHandler<MeetingStartedRequest> {

    private static final Logger log = LoggerFactory.getLogger(MeetingStartedHandler.class);
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingSessionRepository meetingSessionRepository;

    public MeetingStartedHandler(MeetingRepository meetingRepository, UserRepository userRepository, MeetingSessionRepository meetingSessionRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.meetingSessionRepository = meetingSessionRepository;
    }

    @Override
    @Transactional
    public void handle(MeetingStartedRequest request) {
        log.info("Handling meeting.started event for meetingId: {}", request.meeting().id());
        MeetingStartedRequest.MeetingDetail meetingDetail = request.meeting();
        User organizer = findOrCreateUser(meetingDetail.organizedBy());
        Meeting meeting = findOrCreateMeeting(meetingDetail, organizer);
        findOrCreateMeetingSession(meetingDetail.sessionId(), meeting);
        log.info("Meeting with meetingId: {} persisted to the database", meeting.getMeetingId());
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


    private void findOrCreateMeetingSession(String sessionId, Meeting meeting) {
        meetingSessionRepository
                .findBySessionIdAndMeeting(sessionId, meeting)
                .orElseGet(() -> meetingSessionRepository.save(MeetingMapper.toMeetingSessionEntity(sessionId, meeting)));
    }

    @Override
    public String supportedEvent() {
        return "meeting.started";
    }
}
