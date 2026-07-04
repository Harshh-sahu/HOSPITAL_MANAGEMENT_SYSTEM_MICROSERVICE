package com.hms.user.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.user.dto.event.UserLoginEvent;
import com.hms.user.dto.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${hms.kafka.topic.user-registered}")
    private String userRegisteredTopic;

    @Value("${hms.kafka.topic.user-login}")
    private String userLoginTopic;

    public UserEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishUserRegistered(UserRegisteredEvent event) {
        publish(userRegisteredTopic, event.getEmail(), event);
    }

    public void publishUserLogin(UserLoginEvent event) {
        publish(userLoginTopic, event.getEmail(), event);
    }

    private void publish(String topic, String key, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, payload);
            log.info("Published event to topic '{}' for '{}'", topic, key);
        } catch (Exception e) {
            log.error("Failed to publish event to topic '{}': {}", topic, e.getMessage(), e);
        }
    }
}
