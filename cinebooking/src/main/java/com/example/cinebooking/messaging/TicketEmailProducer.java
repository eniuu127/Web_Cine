package com.example.cinebooking.messaging;

import com.example.cinebooking.messaging.event.TicketIssuedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TicketEmailProducer {

  private final RabbitTemplate rabbitTemplate;

  @Value("${app.rabbit.exchange}") private String exchange;
  @Value("${app.rabbit.routingKey}") private String routingKey;

  public TicketEmailProducer(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendTicketPaid(TicketIssuedEvent event) {
    rabbitTemplate.convertAndSend(exchange, routingKey, event);
  }
}
