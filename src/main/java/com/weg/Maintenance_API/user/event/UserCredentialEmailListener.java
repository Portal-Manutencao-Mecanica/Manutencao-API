package com.weg.Maintenance_API.user.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserCredentialEmailListener {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserCredentialEmailListener.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCredentials(UserCreatedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject("Credenciais de acesso ao Portal de Manutenção");
        message.setText("""
                Olá, %s.

                Sua conta foi criada.
                Senha temporária: %s

                A senha deve ser alterada no primeiro acesso e expira em 3 dias.
                Acesse: %s
                """.formatted(event.name(), event.temporaryPassword(), frontendUrl));
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            LOGGER.error(
                    "Falha ao enviar credenciais. userId={}",
                    event.userId(),
                    exception
            );
        }
    }
}
