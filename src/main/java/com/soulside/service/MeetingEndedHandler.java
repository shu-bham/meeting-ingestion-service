package com.soulside.service;

import com.soulside.dto.MeetingEndedRequest;
import org.springframework.stereotype.Service;

@Service
public class MeetingEndedHandler implements MeetingEventHandler<MeetingEndedRequest> {

    @Override
    public void handle(MeetingEndedRequest request) {
        // todo
    }

    @Override
    public String supportedEvent() {
        return "meeting.ended";
    }
}