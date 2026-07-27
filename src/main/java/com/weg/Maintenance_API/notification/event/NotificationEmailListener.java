package com.weg.Maintenance_API.notification.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEmailListener {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(NotificationEmailListener.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String mailFrom;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(NotificationEmailRequestedEvent event) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(mailFrom);
        email.setTo(event.recipientEmail());
        email.setSubject(event.title());
        email.setText(event.message());

        try {
            mailSender.send(email);
        } catch (MailException exception) {
            LOGGER.error(
                    "Falha ao enviar notificação por e-mail. notificationId={}",
                    event.notificationId(),
                    exception
            );
        }
    }
}
