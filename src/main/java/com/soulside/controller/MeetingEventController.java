package com.soulside.controller;

import com.soulside.dto.MeetingEventRequest;
import com.soulside.dto.TranscriptResponse;
import com.soulside.service.EventDeduplicationService;
import com.soulside.service.KafkaProducerService;
import com.soulside.service.MeetingEventCacheService;
import com.soulside.service.TranscriptService;
import com.soulside.util.HashUtil;
import com.soulside.validations.EventStateValidator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    private final EventStateValidator eventStateValidator;
    private final MeetingEventCacheService meetingEventCacheService;
    private final EventDeduplicationService eventDeduplicationService;


    @Value("${kafka.topic}")
    private String meetingEventsTopic;

    public MeetingEventController(TranscriptService transcriptService,
                                  KafkaProducerService kafkaProducerService,
                                  EventStateValidator eventStateValidator,
                                  MeetingEventCacheService meetingEventCacheService,
                                  EventDeduplicationService eventDeduplicationService) {
        this.transcriptService = transcriptService;
        this.kafkaProducerService = kafkaProducerService;
        this.eventStateValidator = eventStateValidator;
        this.meetingEventCacheService = meetingEventCacheService;
        this.eventDeduplicationService = eventDeduplicationService;
    }

    @PostMapping("/webhook/v1")
    @Operation(summary = "Handles incoming meeting events")
    public ResponseEntity<Void> handleEvent(@Valid @RequestBody MeetingEventRequest request) {
        log.info("Received meeting event: {}, key: {}", request.event(), request.getKey());
        var eventHash = HashUtil.toEventHash(request.toString());
        if (eventDeduplicationService.isDuplicate(eventHash)) {
            log.warn("Duplicate meeting event received, event: {}, key: {}", request.event(), request.getKey());
            return ResponseEntity.accepted().build();
        }
        eventDeduplicationService.storeEventHash(eventHash);
        eventStateValidator.validate(request);
        meetingEventCacheService.updateCache(request);
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
