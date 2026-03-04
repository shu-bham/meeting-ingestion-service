package com.soulside.controller;

import com.soulside.dto.MeetingEventRequest;
import com.soulside.dto.TranscriptResponse;
import com.soulside.service.MeetingEventHandlerFactory;
import com.soulside.service.TranscriptService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MeetingEventController {

    private final MeetingEventHandlerFactory factory;
    private final TranscriptService transcriptService;


    public MeetingEventController(MeetingEventHandlerFactory factory, TranscriptService transcriptService) {
        this.factory = factory;
        this.transcriptService = transcriptService;
    }

    @PostMapping("/webhook/v1")
    @Operation(summary = "Handles incoming meeting events")
    public ResponseEntity<Void> handleEvent(@RequestBody MeetingEventRequest request) {
        factory.getHandler(request.event()).handle(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/meetings/{meetingId}/sessions/{sessionId}/transcript")
    @Operation(summary = "Retrieves the transcript for a meeting session")
    public ResponseEntity<TranscriptResponse> getTranscript(@PathVariable String meetingId, @PathVariable String sessionId) {
        return ResponseEntity.ok(transcriptService.getTranscript(meetingId, sessionId));
    }
}
