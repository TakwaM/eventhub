package com.eventhub.reservations_service.service;

import com.eventhub.reservations_service.config.RabbitMQConfig;
import com.eventhub.reservations_service.dto.AnalyticsEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsPublisher {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publish(AnalyticsEvent event) {
        publishInternal(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ANALYTICS_ROUTING_KEY,   // 🔥 ENVOI DANS analytics.queue
                event,
                "analytics event"
        );
    }

    private void publishInternal(String exchange, String routingKey, Object payload, String action) {
        try {
            String message = objectMapper.writeValueAsString(payload);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            logger.debug("[AnalyticsPublisher] published {} to exchange={} routingKey={}",
                    action, exchange, routingKey);
        } catch (Exception e) {
            logger.error("[AnalyticsPublisher] failed to publish {}: {}", action, e.getMessage(), e);
        }
    }
}
