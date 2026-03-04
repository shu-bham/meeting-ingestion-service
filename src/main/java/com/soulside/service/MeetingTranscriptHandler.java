package com.soulside.service;

import com.soulside.dto.MeetingTranscriptRequest;
import org.springframework.stereotype.Service;

@Service
public class MeetingTranscriptHandler implements MeetingEventHandler<MeetingTranscriptRequest> {

    @Override
    public void handle(MeetingTranscriptRequest request) {
        // todo
    }

    @Override
    public String supportedEvent() {
        return "meeting.transcript";
    }
}
