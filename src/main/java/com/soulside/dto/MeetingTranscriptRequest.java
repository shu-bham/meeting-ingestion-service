package com.soulside.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MeetingTranscriptRequest(
        @NotBlank(message = "Event type is required") String event,
        @NotNull(message = "Meeting reference is required") @Valid MeetingRef meeting,
        @NotNull(message = "Transcript data is required") @Valid TranscriptData data)
        implements MeetingEventRequest {

    public record MeetingRef(
            @NotBlank(message = "Meeting ID is required") String id,
            @NotBlank(message = "Session ID is required") String sessionId) {
    }

    public record TranscriptData(
            @NotBlank(message = "Transcript ID is required") String transcriptId,
            int sequenceNumber,
            @NotNull(message = "Speaker details are required") @Valid UserDTO speaker,
            @NotBlank(message = "Content is required") String content,
            int startOffset,
            int endOffset,
            String language) {
    }

    @Override
    public String getKey() {
        return String.join("::", meeting.id(), meeting.sessionId());
    }
}
