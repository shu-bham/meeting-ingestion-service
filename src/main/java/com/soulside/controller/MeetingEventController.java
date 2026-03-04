package com.soulside.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soulside.dto.MeetingEventRequest;
import com.soulside.dto.TranscriptResponse;
import com.soulside.exception.JsonSerializationException;
import com.soulside.service.TranscriptService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class MeetingEventController {

    private static final Logger log = LoggerFactory.getLogger(MeetingEventController.class);
    private final TranscriptService transcriptService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.meeting-events}")
    private String meetingEventsTopic;


    public MeetingEventController(TranscriptService transcriptService,
                                  KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper) {
        this.transcriptService = transcriptService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/webhook/v1")
    @Operation(summary = "Handles incoming meeting events")
    public ResponseEntity<Void> handleEvent(@RequestBody MeetingEventRequest request) {
        log.info("Received meeting event: {}, key: {}", request.event(), request.getKey());
        try {
            String event = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(meetingEventsTopic, request.getKey(), event);
            log.info("Meeting event sent to kafka topic: {}", meetingEventsTopic);
            return ResponseEntity.accepted().build();
        } catch (JsonProcessingException e) {
            log.error("Error serializing meeting event", e);
            throw new JsonSerializationException(e);
        }
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
