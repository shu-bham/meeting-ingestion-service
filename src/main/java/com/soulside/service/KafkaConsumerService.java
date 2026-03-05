package com.soulside.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soulside.dto.MeetingEventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final MeetingEventHandlerFactory eventHandlerFactory;

    public KafkaConsumerService(ObjectMapper objectMapper,
            MeetingEventHandlerFactory eventHandlerFactory) {
        this.eventHandlerFactory = eventHandlerFactory;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(MeetingEventRequest request, Acknowledgment acknowledgment) {
        log.info("Consumed event type: {}", request.event());
        routeToHandler(request);
        acknowledgment.acknowledge();
    }

    private void routeToHandler(MeetingEventRequest request) {
        MeetingEventHandler<MeetingEventRequest> handler = eventHandlerFactory.getHandler(request.event());
        log.info("Routing event {} to handler {}", request.event(), handler.getClass().getSimpleName());
        handler.handle(request);
    }
}

