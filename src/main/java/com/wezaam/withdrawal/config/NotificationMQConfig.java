package com.wezaam.withdrawal.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class NotificationMQConfig {
    public final static String QUEUE = "notification-queue";
    public final static String ROUTING_KEY = "notification";
    public final static String EXCHANGE = "notification-exchange";

    @Bean
    Queue notificationQueue() {
        return new Queue(QUEUE, false);
    }

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(ROUTING_KEY);
    }
}
