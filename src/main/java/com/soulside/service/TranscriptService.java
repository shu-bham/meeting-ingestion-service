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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class TranscriptService {

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
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + meetingId));

        MeetingSession session = meetingSessionRepository.findBySessionIdAndMeeting(sessionId, meeting)
                .orElseThrow(() -> new MeetingSessionNotFoundException("Meeting session not found with ID: " + sessionId));

        List<Transcript> transcripts = transcriptRepository.findBySession(session);

        List<TranscriptResponse.TranscriptEntryResponse> entries = transcripts.stream()
                .sorted(Comparator.comparing(Transcript::getStartOffset))
                .map(this::mapToTranscriptEntryResponse)
                .toList();

        return new TranscriptResponse(meetingId, sessionId, entries);
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
