package com.weg.Maintenance_API.user.event;

import com.weg.Maintenance_API.auth.password.event.PasswordResetRequestedEvent;
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

    // Executa o fluxo de comunicacao ou registro.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCredentials(UserCreatedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject("Credenciais de acesso ao Portal de ManutenÃ§Ã£o");
        message.setText("""
                OlÃ¡, %s.

                Sua conta foi criada.
                Senha temporÃ¡ria: %s

                A senha deve ser alterada no primeiro acesso e expira em 3 dias.
                Acesse: %s
                """.formatted(event.name(), event.temporaryPassword(), frontendUrl));
        send(message, event.userId(), "USER_CREDENTIALS");
    }

    // Executa a operacao deste metodo.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void resendCredentials(TemporaryCredentialsReissuedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject("Novas credenciais do Portal de ManutenÃ§Ã£o");
        message.setText("""
                OlÃ¡, %s.

                Uma nova senha temporÃ¡ria foi emitida para sua conta:
                %s

                A senha anterior nÃ£o Ã© mais vÃ¡lida. Altere a nova senha no primeiro acesso.
                Acesse: %s
                """.formatted(event.name(), event.temporaryPassword(), frontendUrl));
        send(message, event.userId(), "USER_CREDENTIALS_REISSUED");
    }

    // Executa o fluxo de comunicacao ou registro.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordReset(PasswordResetRequestedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject("RecuperaÃ§Ã£o de senha do Portal de ManutenÃ§Ã£o");
        message.setText("""
                OlÃ¡, %s.

                Foi solicitada a recuperaÃ§Ã£o da sua senha.
                Use o link abaixo. Ele Ã© temporÃ¡rio e funciona uma Ãºnica vez:

                %s/password-reset?token=%s

                Se vocÃª nÃ£o solicitou a alteraÃ§Ã£o, ignore esta mensagem.
                """.formatted(event.name(), frontendUrl, event.rawToken()));
        send(message, event.userId(), "PASSWORD_RESET");
    }

    // Executa o fluxo de comunicacao ou registro.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordChanged(PasswordChangedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject("Senha alterada no Portal de ManutenÃ§Ã£o");
        message.setText("""
                OlÃ¡, %s.

                A senha da sua conta foi alterada com sucesso.
                Se vocÃª nÃ£o reconhece esta aÃ§Ã£o, procure o administrador do sistema.
                """.formatted(event.name()));
        send(message, event.userId(), "PASSWORD_CHANGED");
    }

    // Executa o fluxo de comunicacao ou registro.
    private void send(SimpleMailMessage message, java.util.UUID userId, String template) {
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            LOGGER.error(
                    "Falha ao enviar e-mail. userId={}, template={}",
                    userId,
                    template,
                    exception
            );
        }
    }
}
