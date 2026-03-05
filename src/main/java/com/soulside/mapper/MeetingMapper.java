package com.soulside.mapper;

import com.soulside.dto.MeetingStartedRequest;
import com.soulside.dto.MeetingTranscriptRequest;
import com.soulside.dto.UserDTO;
import com.soulside.model.*;

public class MeetingMapper {

    public static User toUserEntity(UserDTO userDto) {
        return new User(userDto.id(), userDto.name());
    }

    public static Meeting toMeetingEntity(MeetingStartedRequest.MeetingDetail meetingDetail, User organizer) {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(meetingDetail.id());
        meeting.setTitle(meetingDetail.title());
        meeting.setRoomName(meetingDetail.roomName());
        meeting.setStatus(meetingDetail.status());
        meeting.setMeetingCreatedAt(meetingDetail.createdAt());
        meeting.setStartedAt(meetingDetail.startedAt());
        meeting.setOrganizer(organizer);
        return meeting;
    }

    public static MeetingSession toMeetingSessionEntity(String sessionId, Meeting meeting) {
        MeetingSession meetingSession = new MeetingSession();
        meetingSession.setSessionId(sessionId);
        meetingSession.setMeeting(meeting);
        return meetingSession;
    }

    public static Transcript toTranscriptEntity(MeetingTranscriptRequest request, SessionParticipant sessionParticipant) {
        MeetingTranscriptRequest.TranscriptData data = request.data();
        Transcript transcript = new Transcript();
        transcript.setTranscriptId(data.transcriptId());
        transcript.setSequenceNumber(data.sequenceNumber());
        transcript.setContent(data.content());
        transcript.setStartOffset(data.startOffset());
        transcript.setEndOffset(data.endOffset());
        transcript.setLanguage(data.language());
        transcript.setSession(sessionParticipant.getSession());
        transcript.setSpeaker(sessionParticipant.getUser());
        return transcript;
    }

    public static SessionParticipant toSessionParticipantEntity(MeetingSession meetingSession, User participant) {
        SessionParticipant sessionParticipant = new SessionParticipant();
        sessionParticipant.setSession(meetingSession);
        sessionParticipant.setUser(participant);
        return sessionParticipant;
    }
}
