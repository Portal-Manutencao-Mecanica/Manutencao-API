package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.MediaType;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.media.entity.Media;
import com.weg.Maintenance_API.media.repository.MediaRepository;
import com.weg.Maintenance_API.media.storage.FileStorageService;
import com.weg.Maintenance_API.media.storage.StoredFile;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.response.ProfilePhotoResponse;
import com.weg.Maintenance_API.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfilePhotoService {

    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;

    @Transactional
    public ProfilePhotoResponse upload(
            String email,
            MultipartFile file,
            ClientRequestMetadata metadata
    ) {
        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
        StoredFile storedFile = fileStorageService.store(file);
        registerRollbackCleanup(storedFile.storageKey());

        Media previous = user.getProfilePhoto();
        if (previous != null) {
            previous.setActive(false);
        }

        Media media = new Media();
        media.setDescription("Foto de perfil");
        media.setMediaType(MediaType.PROFILE_PHOTO);
        media.setImage(storedFile.storageKey());
        media.setOriginalName(storedFile.originalFilename());
        media.setContentType(storedFile.contentType());
        media.setFileSize(storedFile.size());
        media.setUploadedBy(user);
        media.setOrganization(user.getOrganization());
        media.setActive(true);
        mediaRepository.save(media);
        user.setProfilePhoto(media);

        auditService.recordInCurrentTransaction(
                user,
                "PROFILE_PHOTO_UPDATED",
                "MEDIA",
                media.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Foto de perfil atualizada."
        );
        return new ProfilePhotoResponse(
                media.getId(),
                media.getOriginalName(),
                media.getContentType(),
                media.getFileSize()
        );
    }

    private void registerRollbackCleanup(String storageKey) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            fileStorageService.delete(storageKey);
                        }
                    }
                }
        );
    }
}
