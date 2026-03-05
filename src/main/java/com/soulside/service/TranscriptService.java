package com.soulside.service;

import com.soulside.dto.TranscriptResponse;
import com.soulside.exception.MeetingNotFoundException;
import com.soulside.exception.MeetingSessionNotFoundException;
import com.soulside.model.Meeting;
import com.soulside.model.MeetingSession;
import com.soulside.model.Transcript;
import com.soulside.repository.MeetingRepository;
import com.soulside.repository.MeetingSessionRepository;
import com.soulside.repository.TranscriptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class TranscriptService {

    private static final Logger log = LoggerFactory.getLogger(TranscriptService.class);
    private final MeetingRepository meetingRepository;
    private final MeetingSessionRepository meetingSessionRepository;
    private final TranscriptRepository transcriptRepository;

    public TranscriptService(MeetingRepository meetingRepository,
                             MeetingSessionRepository meetingSessionRepository,
                             TranscriptRepository transcriptRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingSessionRepository = meetingSessionRepository;
        this.transcriptRepository = transcriptRepository;
    }

    @Transactional(readOnly = true)
    public TranscriptResponse getTranscript(String meetingId, String sessionId) {
        log.info("Fetching transcript for meetingId: {} and sessionId: {}", meetingId, sessionId);
        Meeting meeting = findMeeting(meetingId);
        MeetingSession session = findMeetingSession(sessionId, meeting);
        List<Transcript> transcripts = transcriptRepository.findBySession(session);
        List<TranscriptResponse.TranscriptEntryResponse> entries = transcripts.stream()
                .sorted(Comparator.comparing(Transcript::getStartOffset))
                .map(this::mapToTranscriptEntryResponse)
                .toList();

        log.info("Found {} transcript entries for meetingId: {} and sessionId: {}", entries.size(), meetingId, sessionId);
        return new TranscriptResponse(meetingId, sessionId, entries);
    }

    private Meeting findMeeting(String meetingId) {
        return meetingRepository
                .findByMeetingId(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + meetingId));
    }

    private MeetingSession findMeetingSession(String sessionId, Meeting meeting) {
        return meetingSessionRepository
                .findBySessionIdAndMeeting(sessionId, meeting)
                .orElseThrow(() -> new MeetingSessionNotFoundException("Meeting session not found with ID: " + sessionId));
    }

    private TranscriptResponse.TranscriptEntryResponse mapToTranscriptEntryResponse(Transcript transcript) {
        TranscriptResponse.SpeakerResponse speaker = new TranscriptResponse.SpeakerResponse(
                transcript.getSpeaker().getUserId(),
                transcript.getSpeaker().getName()
        );

        return new TranscriptResponse.TranscriptEntryResponse(
                transcript.getTranscriptId(),
                transcript.getSequenceNumber(),
                speaker,
                transcript.getContent(),
                transcript.getStartOffset().toString(),
                transcript.getEndOffset().toString(),
                transcript.getLanguage()
        );
    }
}
