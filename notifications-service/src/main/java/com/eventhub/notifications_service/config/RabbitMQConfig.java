package com.eventhub.notifications_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "reservation.created.queue";
    public static final String EXCHANGE = "reservations.exchange";
    public static final String ROUTING_KEY = "reservation.created";

    public static final String CANCEL_QUEUE = "reservation.cancelled.queue";
    public static final String CANCEL_ROUTING_KEY = "reservation.cancelled";


    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

    // JSON converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //  RabbitTemplate avec JSON
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Queue cancelQueue() {
    return new Queue(CANCEL_QUEUE, true);
    }

    @Bean
    public Binding cancelBinding() {
    return BindingBuilder.bind(cancelQueue()).to(exchange()).with(CANCEL_ROUTING_KEY);
    }

}