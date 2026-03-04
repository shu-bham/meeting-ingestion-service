package com.soulside.dto;

public record MeetingTranscriptRequest(
        String event,
        MeetingRef meeting,
        TranscriptData data
) implements MeetingEventRequest {

    public record MeetingRef(
            String id,
            String sessionId
    ) {
    }

    public record TranscriptData(
            String transcriptId,
            int sequenceNumber,
            User speaker,
            String content,
            int startOffset,
            int endOffset,
            String language
    ) {
    }
}
