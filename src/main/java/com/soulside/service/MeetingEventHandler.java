package com.soulside.service;

import com.soulside.dto.MeetingEventRequest;

public interface MeetingEventHandler<T extends MeetingEventRequest> {
    void handle(T request);

    String supportedEvent();
}
