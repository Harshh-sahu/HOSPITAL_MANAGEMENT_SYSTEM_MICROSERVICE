package com.EmailService.EmailServiceMS.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${hms.kafka.topic.user-registered}")
    private String userRegisteredTopic;

    @Value("${hms.kafka.topic.user-login}")
    private String userLoginTopic;

    @Value("${hms.kafka.topic.appointment-created}")
    private String appointmentCreatedTopic;

    @Value("${hms.kafka.topic.prescription-created}")
    private String prescriptionCreatedTopic;

    @Value("${hms.kafka.topic.report-created}")
    private String reportCreatedTopic;

    @Value("${hms.kafka.topic.sale-created}")
    private String saleCreatedTopic;

    @Value("${hms.kafka.topic.doctor-onboarded}")
    private String doctorOnboardedTopic;

    @Value("${hms.kafka.topic.appointment-reminder}")
    private String appointmentReminderTopic;

    @Value("${hms.kafka.topic.followup-reminder}")
    private String followupReminderTopic;

    @Value("${hms.kafka.topic.appointment-cancelled}")
    private String appointmentCancelledTopic;

    @Value("${hms.kafka.topic.low-stock-alert}")
    private String lowStockAlertTopic;

    @Value("${hms.kafka.topic.patient-registered}")
    private String patientRegisteredTopic;

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic userLoginTopic() {
        return TopicBuilder.name(userLoginTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic appointmentCreatedTopic() {
        return TopicBuilder.name(appointmentCreatedTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic prescriptionCreatedTopic() {
        return TopicBuilder.name(prescriptionCreatedTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic reportCreatedTopic() {
        return TopicBuilder.name(reportCreatedTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic saleCreatedTopic() {
        return TopicBuilder.name(saleCreatedTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic doctorOnboardedTopic() {
        return TopicBuilder.name(doctorOnboardedTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic appointmentReminderTopic() {
        return TopicBuilder.name(appointmentReminderTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic followupReminderTopic() {
        return TopicBuilder.name(followupReminderTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic appointmentCancelledTopic() {
        return TopicBuilder.name(appointmentCancelledTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic lowStockAlertTopic() {
        return TopicBuilder.name(lowStockAlertTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic patientRegisteredTopic() {
        return TopicBuilder.name(patientRegisteredTopic).partitions(1).replicas(1).build();
    }
}
