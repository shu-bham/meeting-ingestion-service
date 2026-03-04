package com.soulside.dto;

import java.util.List;

public record TranscriptResponse(
        String meetingId,
        String sessionId,
        List<TranscriptEntryResponse> entries
) {
    public record TranscriptEntryResponse(
            String transcriptId,
            Integer sequenceNumber,
            SpeakerResponse speaker,
            String content,
            String startOffset,
            String endOffset,
            String language
    ) {
    }

    public record SpeakerResponse(
            String id,
            String name
    ) {
    }
}