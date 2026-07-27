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
        send(message, event.userId(), "USER_CREDENTIALS");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void resendCredentials(TemporaryCredentialsReissuedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject("Novas credenciais do Portal de Manutenção");
        message.setText("""
                Olá, %s.

                Uma nova senha temporária foi emitida para sua conta:
                %s

                A senha anterior não é mais válida. Altere a nova senha no primeiro acesso.
                Acesse: %s
                """.formatted(event.name(), event.temporaryPassword(), frontendUrl));
        send(message, event.userId(), "USER_CREDENTIALS_REISSUED");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordReset(PasswordResetRequestedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject("Recuperação de senha do Portal de Manutenção");
        message.setText("""
                Olá, %s.

                Foi solicitada a recuperação da sua senha.
                Use o link abaixo. Ele é temporário e funciona uma única vez:

                %s/password-reset?token=%s

                Se você não solicitou a alteração, ignore esta mensagem.
                """.formatted(event.name(), frontendUrl, event.rawToken()));
        send(message, event.userId(), "PASSWORD_RESET");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordChanged(PasswordChangedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject("Senha alterada no Portal de Manutenção");
        message.setText("""
                Olá, %s.

                A senha da sua conta foi alterada com sucesso.
                Se você não reconhece esta ação, procure o administrador do sistema.
                """.formatted(event.name()));
        send(message, event.userId(), "PASSWORD_CHANGED");
    }

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
