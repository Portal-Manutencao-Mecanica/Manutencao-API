package com.weg.Maintenance_API.notification.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.weg.Maintenance_API.notification.entity.Notification;
import com.weg.Maintenance_API.notification.event.NotificationEmailListener;
import com.weg.Maintenance_API.notification.event.NotificationEmailRequestedEvent;
import com.weg.Maintenance_API.notification.mapper.NotificationMapper;
import com.weg.Maintenance_API.notification.repository.NotificationRepository;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.user.preference.entity.NotificationPreference;
import com.weg.Maintenance_API.user.preference.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryServiceTest {

    @Mock private NotificationMapper mapper;
    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationPreferenceRepository preferenceRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private JavaMailSender mailSender;
    @InjectMocks private NotificationService service;

    @Test
    void emailDisabledKeepsOnlyInAppNotification() {
        Student user = user("student@example.test");
        NotificationPreference preference = new NotificationPreference(user);
        preference.setEmailEnabled(false);
        preference.setInAppEnabled(true);
        when(preferenceRepository.findByUserId(user.getId())).thenReturn(Optional.of(preference));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.notifyUser(user, "Title", "About", "Message");

        verify(notificationRepository).save(any(Notification.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void emailEventUsesRecipientEmail() {
        Student user = user("recipient@example.test");
        NotificationPreference preference = new NotificationPreference(user);
        preference.setEmailEnabled(true);
        preference.setInAppEnabled(false);
        when(preferenceRepository.findByUserId(user.getId())).thenReturn(Optional.of(preference));
        ArgumentCaptor<NotificationEmailRequestedEvent> events = ArgumentCaptor.forClass(NotificationEmailRequestedEvent.class);

        service.notifyUser(user, "Title", "About", "Message");

        verify(eventPublisher).publishEvent(events.capture());
        org.junit.jupiter.api.Assertions.assertEquals("recipient@example.test", events.getValue().recipientEmail());
    }

    @Test
    void mailProviderFailureDoesNotEscapeListener() {
        NotificationEmailListener listener = new NotificationEmailListener(mailSender);
        ReflectionTestUtils.setField(listener, "mailFrom", "no-reply@example.test");
        org.mockito.Mockito.doThrow(new MailSendException("offline")).when(mailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        assertDoesNotThrow(() -> listener.send(new NotificationEmailRequestedEvent(null, "recipient@example.test", "Title", "Message")));
    }

    private Student user(String email) {
        Student user = new Student();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        return user;
    }
}