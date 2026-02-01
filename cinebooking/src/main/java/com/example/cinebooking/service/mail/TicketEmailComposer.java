package com.example.cinebooking.service.mail;

import com.example.cinebooking.DTO.Event.TicketIssuedEvent;
import com.example.cinebooking.Util.QrCodeUtil;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
public class TicketEmailComposer {

    private final TemplateEngine templateEngine;

    public TicketEmailComposer(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public Result compose(TicketIssuedEvent ev) {
        Context ctx = new Context();
        ctx.setVariable("ev", ev);

        String html = templateEngine.process("mail/ticket-email", ctx);

        Map<String, byte[]> inline = new HashMap<>();
        for (int i = 0; i < ev.tickets.size(); i++) {
            String cid = "qr_" + i; // khớp template cid:qr_i
            String qrText = ev.tickets.get(i).qrContent;
            inline.put(cid, QrCodeUtil.toPngBytes(qrText, 300));
        }

        return new Result(html, inline);
    }

    public record Result(String html, Map<String, byte[]> inlinePngByCid) {}
}
