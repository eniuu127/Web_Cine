package com.example.cinebooking.messaging;

import com.example.cinebooking.config.RabbitMQConfig;
import com.example.cinebooking.DTO.Event.TicketIssuedEvent;
import com.example.cinebooking.service.mail.MailService;
import com.example.cinebooking.service.mail.TicketEmailComposer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TicketEventListener {

    private final MailService mailService;
    private final TicketEmailComposer composer;

    public TicketEventListener(MailService mailService, TicketEmailComposer composer) {
        this.mailService = mailService;
        this.composer = composer;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void onTicketIssued(TicketIssuedEvent ev) {
        var result = composer.compose(ev);

        mailService.sendHtmlWithInlineImages(
                ev.toEmail,
                "Vé xem phim - " + ev.bookingCode,
                result.html(),
                result.inlinePngByCid()
        );
    }
}
