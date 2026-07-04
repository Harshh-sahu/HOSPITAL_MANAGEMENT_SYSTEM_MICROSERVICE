package com.hms.pharmacy.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.pharmacy.dto.event.LowStockAlertEvent;
import com.hms.pharmacy.dto.event.SaleCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PharmacyEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PharmacyEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${hms.kafka.topic.sale-created}")
    private String saleCreatedTopic;

    @Value("${hms.kafka.topic.low-stock-alert}")
    private String lowStockAlertTopic;

    public PharmacyEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSaleCreated(SaleCreatedEvent event) {
        publish(saleCreatedTopic, event.getBuyerEmail(), event);
    }

    public void publishLowStockAlert(LowStockAlertEvent event) {
        publish(lowStockAlertTopic, String.valueOf(event.getMedicineId()), event);
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
