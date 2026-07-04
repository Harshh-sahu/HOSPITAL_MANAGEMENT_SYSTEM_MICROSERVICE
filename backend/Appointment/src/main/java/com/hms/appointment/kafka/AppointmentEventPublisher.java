package com.hms.appointment.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.appointment.dto.event.AppointmentCancelledEvent;
import com.hms.appointment.dto.event.AppointmentCreatedEvent;
import com.hms.appointment.dto.event.AppointmentReminderEvent;
import com.hms.appointment.dto.event.FollowUpReminderEvent;
import com.hms.appointment.dto.event.PrescriptionCreatedEvent;
import com.hms.appointment.dto.event.ReportCreatedEvent;
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

    @Value("${hms.kafka.topic.report-created}")
    private String reportCreatedTopic;

    @Value("${hms.kafka.topic.appointment-reminder}")
    private String appointmentReminderTopic;

    @Value("${hms.kafka.topic.followup-reminder}")
    private String followupReminderTopic;

    @Value("${hms.kafka.topic.appointment-cancelled}")
    private String appointmentCancelledTopic;

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

    public void publishReportCreated(ReportCreatedEvent event) {
        publish(reportCreatedTopic, event.getPatientEmail(), event);
    }

    public void publishAppointmentReminder(AppointmentReminderEvent event) {
        publish(appointmentReminderTopic, event.getPatientEmail(), event);
    }

    public void publishFollowUpReminder(FollowUpReminderEvent event) {
        publish(followupReminderTopic, event.getPatientEmail(), event);
    }

    public void publishAppointmentCancelled(AppointmentCancelledEvent event) {
        publish(appointmentCancelledTopic, event.getPatientEmail(), event);
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
