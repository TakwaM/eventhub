package com.eventhub.reservations_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "reservations.exchange";

    // Réservations (confirmations)
    public static final String ROUTING_KEY = "reservation.created";
    public static final String QUEUE = "reservation.created.queue";

    // Annulations
    public static final String CANCEL_QUEUE = "reservation.cancelled.queue";
    public static final String CANCEL_ROUTING_KEY = "reservation.cancelled";

    // 🔥 Nouvelle queue dédiée pour analytics
    public static final String ANALYTICS_QUEUE = "analytics.queue";
    public static final String ANALYTICS_ROUTING_KEY = "analytics.created";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Queue cancelQueue() {
        return new Queue(CANCEL_QUEUE, true);
    }

    @Bean
    public Binding cancelBinding() {
        return BindingBuilder
                .bind(cancelQueue())
                .to(exchange())
                .with(CANCEL_ROUTING_KEY);
    }

    // 🔥 Nouvelle queue analytics
    @Bean
    public Queue analyticsQueue() {
        return new Queue(ANALYTICS_QUEUE, true);
    }

    @Bean
    public Binding analyticsBinding() {
        return BindingBuilder
                .bind(analyticsQueue())
                .to(exchange())
                .with(ANALYTICS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
