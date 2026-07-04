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
}
