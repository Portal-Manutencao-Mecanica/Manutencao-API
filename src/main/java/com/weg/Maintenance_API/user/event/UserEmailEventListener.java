package com.weg.Maintenance_API.user.event;

import com.weg.Maintenance_API.auth.firstaccess.event.FirstAccessCodeRequestedEvent;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserEmailEventListener {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserEmailEventListener.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCredentials(UserCreatedEvent event) {
        SimpleMailMessage message = createMessage(
                event.email(),
                "Credenciais de acesso ao Portal de Manutenção",
                """
                Olá, %s.

                Sua conta foi criada.

                Senha temporária: %s

                A senha deve ser alterada no primeiro acesso e expira em 3 dias.

                Acesse: %s
                """.formatted(
                        event.name(),
                        event.temporaryPassword(),
                        normalizedFrontendUrl()
                )
        );

        send(message, event.userId(), "USER_CREDENTIALS");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void resendCredentials(TemporaryCredentialsReissuedEvent event) {
        SimpleMailMessage message = createMessage(
                event.email(),
                "Novas credenciais do Portal de Manutenção",
                """
                Olá, %s.

                Uma nova senha temporária foi emitida para sua conta:

                %s

                A senha anterior não é mais válida.
                Altere a nova senha no primeiro acesso.

                Acesse: %s
                """.formatted(
                        event.name(),
                        event.temporaryPassword(),
                        normalizedFrontendUrl()
                )
        );

        send(message, event.userId(), "USER_CREDENTIALS_REISSUED");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordReset(PasswordResetRequestedEvent event) {
        String encodedToken = URLEncoder.encode(
                event.rawToken(),
                StandardCharsets.UTF_8
        );

        String resetUrl = "%s/password-reset?token=%s"
                .formatted(normalizedFrontendUrl(), encodedToken);

        SimpleMailMessage message = createMessage(
                event.email(),
                "Recuperação de senha do Portal de Manutenção",
                """
                Olá, %s.

                Foi solicitada a recuperação da sua senha.

                Use o link abaixo. Ele é temporário e funciona uma única vez:

                %s

                Se você não solicitou a alteração, ignore esta mensagem.
                """.formatted(event.name(), resetUrl)
        );

        send(message, event.userId(), "PASSWORD_RESET");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordChanged(PasswordChangedEvent event) {
        SimpleMailMessage message = createMessage(
                event.email(),
                "Senha alterada no Portal de Manutenção",
                """
                Olá, %s.

                A senha da sua conta foi alterada com sucesso.

                Se você não reconhece esta ação, procure o administrador do sistema.
                """.formatted(event.name())
        );

        send(message, event.userId(), "PASSWORD_CHANGED");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendFirstAccessCode(FirstAccessCodeRequestedEvent event) {
        SimpleMailMessage message = createMessage(
                event.email(),
                "Código de verificação do primeiro acesso",
                """
                Olá, %s.

                Seu código para cadastrar a senha definitiva é:

                %s

                O código expira em 10 minutos e só pode ser usado uma vez.

                Se você não solicitou este código, procure o administrador do sistema.
                """.formatted(event.name(), event.code())
        );

        send(message, event.userId(), "FIRST_ACCESS_CODE");
    }

    private SimpleMailMessage createMessage(
            String recipient,
            String subject,
            String text
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    private String normalizedFrontendUrl() {
        if (frontendUrl.endsWith("/")) {
            return frontendUrl.substring(0, frontendUrl.length() - 1);
        }

        return frontendUrl;
    }

    private void send(
            SimpleMailMessage message,
            UUID userId,
            String template
    ) {
        try {
            mailSender.send(message);

            LOGGER.info(
                    "E-mail enviado com sucesso. userId={}, template={}",
                    userId,
                    template
            );
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