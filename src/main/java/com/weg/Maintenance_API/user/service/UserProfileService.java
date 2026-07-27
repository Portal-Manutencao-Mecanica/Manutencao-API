package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.request.UpdateNotificationPreferencesRequest;
import com.weg.Maintenance_API.user.dto.request.UpdateOwnProfileRequest;
import com.weg.Maintenance_API.user.dto.response.NotificationPreferenceResponse;
import com.weg.Maintenance_API.user.dto.response.UserProfileResponse;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.preference.entity.NotificationPreference;
import com.weg.Maintenance_API.user.preference.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserIdentityPolicy identityPolicy;
    private final AuditService auditService;

    // Busca os dados necessarios para esta operacao.
    @Transactional
    public UserProfileResponse get(String email) {
        User user = getRequired(email);
        return response(user, preferenceFor(user));
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public UserProfileResponse update(
            String email,
            UpdateOwnProfileRequest request,
            ClientRequestMetadata metadata
    ) {
        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new ResourceNotFoundException("UsuÃ¡rio autenticado"));
        identityPolicy.validateName(request.name());
        user.setName(request.name().trim());
        NotificationPreference preference = preferenceFor(user);
        auditService.recordInCurrentTransaction(
                user,
                "USER_PROFILE_UPDATED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Nome de exibiÃ§Ã£o atualizado."
        );
        return response(user, preference);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public UserProfileResponse updatePreferences(
            String email,
            UpdateNotificationPreferencesRequest request,
            ClientRequestMetadata metadata
    ) {
        if (request.isEmpty()) {
            throw new InvalidRequestException(
                    "Informe ao menos uma preferÃªncia para atualizaÃ§Ã£o."
            );
        }
        User user = getRequired(email);
        NotificationPreference preference = preferenceFor(user);
        if (request.emailEnabled() != null) {
            preference.setEmailEnabled(request.emailEnabled());
        }
        if (request.inAppEnabled() != null) {
            preference.setInAppEnabled(request.inAppEnabled());
        }
        if (request.occurrenceNotifications() != null) {
            preference.setOccurrenceNotifications(request.occurrenceNotifications());
        }
        if (request.purchaseNotifications() != null) {
            preference.setPurchaseNotifications(request.purchaseNotifications());
        }
        if (request.inspectionNotifications() != null) {
            preference.setInspectionNotifications(request.inspectionNotifications());
        }
        auditService.recordInCurrentTransaction(
                user,
                "NOTIFICATION_PREFERENCES_UPDATED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "PreferÃªncias de notificaÃ§Ã£o atualizadas."
        );
        return response(user, preference);
    }

    // Executa a operacao deste metodo.
    public UserProfileResponse response(
            User user,
            NotificationPreference preference
    ) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.isPasswordChangeRequired(),
                new OrganizationSummaryResponse(
                        user.getOrganization().getId(),
                        user.getOrganization().getName()
                ),
                preference(preference),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Busca os dados necessarios para esta operacao.
    private User getRequired(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("UsuÃ¡rio autenticado"));
    }

    // Define valores padrao antes da persistencia.
    private NotificationPreference preferenceFor(User user) {
        return preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> preferenceRepository.save(
                        new NotificationPreference(user)
                ));
    }

    // Define valores padrao antes da persistencia.
    private NotificationPreferenceResponse preference(
            NotificationPreference preference
    ) {
        return new NotificationPreferenceResponse(
                preference.isEmailEnabled(),
                preference.isInAppEnabled(),
                preference.isOccurrenceNotifications(),
                preference.isPurchaseNotifications(),
                preference.isInspectionNotifications()
        );
    }
}
