package com.soulside.service;

import com.soulside.dto.MeetingStartedRequest;
import org.springframework.stereotype.Service;

@Service
public class MeetingStartedHandler implements MeetingEventHandler<MeetingStartedRequest> {

    @Override
    public void handle(MeetingStartedRequest request) {
        // todo
    }

    @Override
    public String supportedEvent() {
        return "meeting.started";
    }
}
