package com.wezaam.withdrawal.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TransactionMQConfig {
    public final static String QUEUE = "transaction-queue";
    public final static String ROUTING_KEY = "transaction";
    public final static String EXCHANGE = "transaction-exchange";

    @Bean
    Queue transactionQueue() {
        return new Queue(QUEUE, false);
    }

    @Bean
    TopicExchange transactionExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Binding transactionBinding(Queue transactionQueue, TopicExchange transactionExchange) {
        return BindingBuilder.bind(transactionQueue).to(transactionExchange).with(ROUTING_KEY);
    }
}
