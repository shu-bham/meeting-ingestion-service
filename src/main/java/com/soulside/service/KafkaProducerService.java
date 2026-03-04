package com.soulside.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soulside.exception.JsonSerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> void sendMessage(String topic, String key, T payload) {
        try {
            String event = objectMapper.writeValueAsString(payload);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent with offset=[{}]", result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send due to : {}", ex.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing message", e);
            throw new JsonSerializationException(e);
        }
    }
}
