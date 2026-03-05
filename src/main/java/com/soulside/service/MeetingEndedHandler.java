package com.soulside.service;

import com.soulside.dto.MeetingEndedRequest;
import org.springframework.stereotype.Service;

@Service
public class MeetingEndedHandler implements MeetingEventHandler<MeetingEndedRequest> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MeetingEndedHandler.class);

    @Override
    public void handle(MeetingEndedRequest request) {
        log.info("Handling meeting ended event: {}", request);
    }

    @Override
    public String supportedEvent() {
        return "meeting.ended";
    }
}