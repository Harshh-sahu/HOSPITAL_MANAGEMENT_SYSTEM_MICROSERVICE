package com.hms.profile.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.profile.dto.event.DoctorOnboardedEvent;
import com.hms.profile.dto.event.PatientRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProfileEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProfileEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${hms.kafka.topic.doctor-onboarded}")
    private String doctorOnboardedTopic;

    @Value("${hms.kafka.topic.patient-registered}")
    private String patientRegisteredTopic;

    public ProfileEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishDoctorOnboarded(DoctorOnboardedEvent event) {
        publish(doctorOnboardedTopic, event.getEmail(), event);
    }

    public void publishPatientRegistered(PatientRegisteredEvent event) {
        publish(patientRegisteredTopic, event.getEmail(), event);
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
