package com.soulside.controller;

import com.soulside.dto.MeetingEventRequest;
import com.soulside.service.MeetingEventHandlerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetingEventController {

    private final MeetingEventHandlerFactory factory;

    public MeetingEventController(MeetingEventHandlerFactory factory) {
        this.factory = factory;
    }

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.accepted().body("hello world");
    }

    @PostMapping("/webhook/v1")
    public ResponseEntity<Void> handleEvent(@RequestBody MeetingEventRequest request) {
        factory.getHandler(request.event()).handle(request);
        return ResponseEntity.ok().build();
    }
}
