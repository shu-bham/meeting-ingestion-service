package com.soulside.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.soulside.util.MeetingEventConstants;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "event",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MeetingStartedRequest.class, name = MeetingEventConstants.MEETING_STARTED),
        @JsonSubTypes.Type(value = MeetingTranscriptRequest.class, name = MeetingEventConstants.MEETING_TRANSCRIPT),
        @JsonSubTypes.Type(value = MeetingEndedRequest.class, name = MeetingEventConstants.MEETING_ENDED)
})
public sealed interface MeetingEventRequest permits MeetingStartedRequest, MeetingTranscriptRequest, MeetingEndedRequest {
    String event();

    String getKey();
}
