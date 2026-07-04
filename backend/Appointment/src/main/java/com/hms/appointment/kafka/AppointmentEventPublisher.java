package com.hms.appointment.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.appointment.dto.event.AppointmentCreatedEvent;
import com.hms.appointment.dto.event.PrescriptionCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${hms.kafka.topic.appointment-created}")
    private String appointmentCreatedTopic;

    @Value("${hms.kafka.topic.prescription-created}")
    private String prescriptionCreatedTopic;

    public AppointmentEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishAppointmentCreated(AppointmentCreatedEvent event) {
        publish(appointmentCreatedTopic, event.getPatientEmail(), event);
    }

    public void publishPrescriptionCreated(PrescriptionCreatedEvent event) {
        publish(prescriptionCreatedTopic, event.getPatientEmail(), event);
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
