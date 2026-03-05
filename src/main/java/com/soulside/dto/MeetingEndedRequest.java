package com.soulside.dto;

import java.time.Instant;

public record MeetingEndedRequest(
        String event,
        MeetingDetail meeting,
        String reason
) implements MeetingEventRequest {

    public record MeetingDetail(
            String id,
            String sessionId,
            String title,
            String status,
            Instant createdAt,
            Instant startedAt,
            Instant endedAt,
            UserDTO organizedBy
    ) {
    }

    @Override
    public String getKey() {
        return meeting.id() + "_" + meeting.sessionId();
    }
}
