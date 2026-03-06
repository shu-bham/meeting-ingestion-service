package com.soulside.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record MeetingEndedRequest(
        @NotBlank(message = "Event type is required") String event,
        @NotNull(message = "Meeting details are required") @Valid MeetingDetail meeting,
        String reason) implements MeetingEventRequest {

    public record MeetingDetail(
            @NotBlank(message = "Meeting ID is required") String id,
            @NotBlank(message = "Session ID is required") String sessionId,
            String title,
            String status,
            Instant createdAt,
            Instant startedAt,
            Instant endedAt,
            @NotNull(message = "Organizer details are required") @Valid UserDTO organizedBy) {
    }

    @Override
    public String getKey() {
        return String.join("::", meeting.id(), meeting.sessionId());
    }
}
