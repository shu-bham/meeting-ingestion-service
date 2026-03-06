package com.soulside.config;

import com.soulside.exception.*;
import org.apache.kafka.common.errors.DisconnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import java.net.ConnectException;
import java.net.UnknownHostException;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    @Value("${kafka.dlq-topic}")
    private String dlqTopic;

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(100000L);
        backOff.setMaxElapsedTime(10800000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(
                JsonSerializationException.class,
                InvalidEventOrderException.class,
                MeetingNotFoundException.class,
                MeetingSessionNotFoundException.class);
        errorHandler.addRetryableExceptions(
                KafkaConnectionException.class,
                ConnectException.class,
                DisconnectException.class,
                UnknownHostException.class
        );
        return errorHandler;
    }

    @Bean
    public DeadLetterPublishingRecoverer recoverer(KafkaTemplate<String, Object> template) {
        return new DeadLetterPublishingRecoverer(template, (record, exception) -> {
            log.error("Exhausted retries for record. Publishing to DLQ topic {}. Exception: ", dlqTopic, exception);
            return new org.apache.kafka.common.TopicPartition(dlqTopic, -1);
        });
    }
}
