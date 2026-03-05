package com.soulside.dto;

import java.time.Instant;

public record MeetingStartedRequest(
        String event,
        MeetingDetail meeting
) implements MeetingEventRequest {

    public record MeetingDetail(
            String id,
            String sessionId,
            String title,
            String roomName,
            String status,
            Instant createdAt,
            Instant startedAt,
            UserDTO organizedBy
    ) {
    }

    @Override
    public String getKey() {
        return meeting.id() + "_" + meeting.sessionId();
    }
}
