package com.example.cinebooking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "cine.ticket.exchange";
    public static final String QUEUE = "cine.ticket.issued.queue";
    public static final String ROUTING_KEY = "ticket.issued";

    @Bean
    public DirectExchange ticketExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue ticketIssuedQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding ticketIssuedBinding(Queue ticketIssuedQueue, DirectExchange ticketExchange) {
        return BindingBuilder.bind(ticketIssuedQueue).to(ticketExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter converter) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(converter);
        return tpl;
    }
}
