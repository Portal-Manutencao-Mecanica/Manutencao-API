package com.weg.Maintenance_API.user.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEmailEventListenerTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void sendsTheNewUsersTemporaryPasswordToTheirEmail() {
        UserEmailEventListener listener = new UserEmailEventListener(mailSender);
        ReflectionTestUtils.setField(listener, "from", "no-reply@portal.test");
        ReflectionTestUtils.setField(listener, "frontendUrl", "https://portal.test");

        listener.sendCredentials(new UserCreatedEvent(
                UUID.randomUUID(),
                "Maria da Silva",
                "maria@portal.test",
                "Temp@1234Ab"
        ));

        ArgumentCaptor<SimpleMailMessage> messageCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("no-reply@portal.test", message.getFrom());
        assertEquals("maria@portal.test", message.getTo()[0]);
        assertTrue(message.getText().contains("Temp@1234Ab"));
        assertTrue(message.getText().contains("https://portal.test"));
    }
}
