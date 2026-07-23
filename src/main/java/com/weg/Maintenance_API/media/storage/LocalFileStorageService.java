package com.weg.Maintenance_API.media.storage;

import com.weg.Maintenance_API.exception.type.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png");
    private static final Map<String, String> EXTENSIONS =
            Map.of("image/jpeg", ".jpg", "image/png", ".png");

    private final Path root;
    private final long maxSize;

    public LocalFileStorageService(
            @Value("${app.file-storage.path:./storage}") String storagePath,
            @Value("${app.file-storage.profile-photo-max-size-bytes:5242880}") long maxSize
    ) {
        this.root = Path.of(storagePath).toAbsolutePath().normalize();
        this.maxSize = maxSize;
    }

    @Override
    public StoredFile store(MultipartFile file) {
        validate(file);
        String contentType = file.getContentType().toLowerCase(Locale.ROOT);
        String storageKey = "profile/" + UUID.randomUUID() + EXTENSIONS.get(contentType);
        Path destination = resolve(storageKey);
        try {
            Files.createDirectories(destination.getParent());
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination);
            }
            return new StoredFile(
                    storageKey,
                    safeOriginalName(file.getOriginalFilename()),
                    contentType,
                    file.getSize()
            );
        } catch (IOException exception) {
            throw new InvalidFileException(
                    "Não foi possível armazenar a foto de perfil.",
                    exception
            );
        }
    }

    @Override
    public Resource load(String storageKey) {
        try {
            Resource resource = new UrlResource(resolve(storageKey).toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new InvalidFileException("O arquivo solicitado não está disponível.");
            }
            return resource;
        } catch (MalformedURLException exception) {
            throw new InvalidFileException("O caminho do arquivo é inválido.", exception);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException exception) {
            throw new InvalidFileException(
                    "Não foi possível remover o arquivo armazenado.",
                    exception
            );
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("A foto de perfil é obrigatória.");
        }
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("A foto excede o tamanho máximo permitido.");
        }
        String contentType = file.getContentType();
        if (contentType == null
                || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new InvalidFileException("Somente imagens JPEG e PNG são permitidas.");
        }
        try (InputStream input = file.getInputStream()) {
            byte[] signature = input.readNBytes(8);
            if (!matchesSignature(contentType.toLowerCase(Locale.ROOT), signature)) {
                throw new InvalidFileException(
                        "O conteúdo do arquivo não corresponde ao tipo informado."
                );
            }
        } catch (IOException exception) {
            throw new InvalidFileException("Não foi possível validar o arquivo.", exception);
        }
    }

    private boolean matchesSignature(String contentType, byte[] bytes) {
        if ("image/png".equals(contentType)) {
            byte[] png = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            return java.util.Arrays.equals(bytes, png);
        }
        return bytes.length >= 3
                && bytes[0] == (byte) 0xFF
                && bytes[1] == (byte) 0xD8
                && bytes[2] == (byte) 0xFF;
    }

    private Path resolve(String storageKey) {
        Path resolved = root.resolve(storageKey).normalize();
        if (!resolved.startsWith(root)) {
            throw new InvalidFileException("Caminho de arquivo não permitido.");
        }
        return resolved;
    }

    private String safeOriginalName(String filename) {
        if (filename == null || filename.isBlank()) {
            return "profile-image";
        }
        try {
            return Path.of(filename).getFileName().toString();
        } catch (RuntimeException exception) {
            return "profile-image";
        }
    }
}
