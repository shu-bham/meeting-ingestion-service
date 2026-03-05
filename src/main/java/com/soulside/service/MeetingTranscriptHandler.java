package com.soulside.service;

import com.soulside.dto.MeetingTranscriptRequest;
import org.springframework.stereotype.Service;

@Service
public class MeetingTranscriptHandler implements MeetingEventHandler<MeetingTranscriptRequest> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MeetingTranscriptHandler.class);

    @Override
    public void handle(MeetingTranscriptRequest request) {
        log.info("Handling meeting transcript event: {}", request);
    }

    @Override
    public String supportedEvent() {
        return "meeting.transcript";
    }
}
