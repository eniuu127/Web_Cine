package com.example.cinebooking.service.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHtmlWithInlineImages(String to, String subject, String html,
                                         java.util.Map<String, byte[]> inlinePngByCid) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            for (var e : inlinePngByCid.entrySet()) {
                helper.addInline(e.getKey(), new ByteArrayResource(e.getValue()), "image/png");
            }

            mailSender.send(msg);
        } catch (Exception ex) {
            throw new RuntimeException("Send mail failed", ex);
        }
    }
}
