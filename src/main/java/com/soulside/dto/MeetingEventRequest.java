package com.soulside.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "event",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MeetingStartedRequest.class, name = "meeting.started"),
        @JsonSubTypes.Type(value = MeetingTranscriptRequest.class, name = "meeting.transcript"),
        @JsonSubTypes.Type(value = MeetingEndedRequest.class, name = "meeting.ended")
})
public sealed interface MeetingEventRequest
        permits MeetingStartedRequest, MeetingTranscriptRequest, MeetingEndedRequest {
    String event();
}