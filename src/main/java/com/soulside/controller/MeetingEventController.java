package com.soulside.controller;

import com.soulside.dto.MeetingEventRequest;
import com.soulside.dto.TranscriptResponse;
import com.soulside.service.KafkaProducerService;
import com.soulside.service.TranscriptService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MeetingEventController {

    private static final Logger log = LoggerFactory.getLogger(MeetingEventController.class);
    private final TranscriptService transcriptService;
    private final KafkaProducerService kafkaProducerService;

    @Value("${kafka.topic.meeting-events}")
    private String meetingEventsTopic;


    public MeetingEventController(TranscriptService transcriptService,
                                  KafkaProducerService kafkaProducerService) {
        this.transcriptService = transcriptService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/webhook/v1")
    @Operation(summary = "Handles incoming meeting events")
    public ResponseEntity<Void> handleEvent(@RequestBody MeetingEventRequest request) {
        log.info("Received meeting event: {}, key: {}", request.event(), request.getKey());
        kafkaProducerService.sendMessage(meetingEventsTopic, request.getKey(), request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/meetings/{meetingId}/sessions/{sessionId}/transcript")
    @Operation(summary = "Retrieves the transcript for a meeting session")
    public ResponseEntity<TranscriptResponse> getTranscript(@PathVariable String meetingId, @PathVariable String sessionId) {
        log.info("Received request to get transcript for meetingId: {} and sessionId: {}", meetingId, sessionId);
        TranscriptResponse transcript = transcriptService.getTranscript(meetingId, sessionId);
        log.info("Returning transcript for meetingId: {} and sessionId: {}", meetingId, sessionId);
        return ResponseEntity.ok(transcript);
    }
}
