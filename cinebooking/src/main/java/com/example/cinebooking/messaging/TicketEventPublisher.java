package com.example.cinebooking.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.cinebooking.DTO.Event.TicketIssuedEvent;

@Component
public class TicketEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange}")
    private String exchange;

    @Value("${app.rabbit.routingKey}")
    private String routingKey;

    public TicketEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTicketIssued(TicketIssuedEvent ev) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, ev);
        } catch (AmqpException ex) {
            //  RabbitMQ down -> KHÔNG làm fail payment/booking
            System.out.println("[WARN] RabbitMQ down, skip publish ticket.issued: " + ex.getMessage());
        }
    }
}
