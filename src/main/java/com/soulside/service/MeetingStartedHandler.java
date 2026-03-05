package com.soulside.service;

import com.soulside.dto.MeetingStartedRequest;
import org.springframework.stereotype.Service;

@Service
public class MeetingStartedHandler implements MeetingEventHandler<MeetingStartedRequest> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MeetingStartedHandler.class);

    @Override
    public void handle(MeetingStartedRequest request) {
        log.info("Handling meeting started event: {}", request);
    }

    @Override
    public String supportedEvent() {
        return "meeting.started";
    }
}
