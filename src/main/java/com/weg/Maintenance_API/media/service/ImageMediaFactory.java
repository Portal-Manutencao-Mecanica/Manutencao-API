package com.weg.Maintenance_API.media.service;

import com.weg.Maintenance_API.enums.MediaType;
import com.weg.Maintenance_API.exception.type.InvalidFileException;
import com.weg.Maintenance_API.media.entity.Media;
import com.weg.Maintenance_API.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ImageMediaFactory {

    private static final int MAX_IMAGES = 5;
    private static final int MAX_IMAGE_BYTES = 5 * 1024 * 1024;
    private static final Pattern IMAGE_DATA_URL = Pattern.compile(
            "^data:(image/(?:png|jpeg|webp|svg\\+xml));base64,([A-Za-z0-9+/=]+)$"
    );

    public List<Media> fromDataUrls(
            List<String> images,
            User uploadedBy,
            MediaType mediaType,
            String filePrefix,
            String description,
            boolean required
    ) {
        if (images == null || images.isEmpty()) {
            if (required) {
                throw new InvalidFileException("Anexe pelo menos uma imagem.");
            }
            return new ArrayList<>();
        }
        if (images.size() > MAX_IMAGES) {
            throw new InvalidFileException("Envie no máximo 5 imagens.");
        }

        List<Media> media = new ArrayList<>();
        for (int index = 0; index < images.size(); index++) {
            Matcher matcher = matcher(images.get(index));
            byte[] bytes = decodedBytes(matcher.group(2));
            if (bytes.length == 0 || bytes.length > MAX_IMAGE_BYTES) {
                throw new InvalidFileException("Cada imagem deve ter no máximo 5 MB.");
            }

            Media item = new Media();
            item.setMediaType(mediaType);
            item.setImage(images.get(index));
            item.setContentType(matcher.group(1));
            item.setOriginalName(filePrefix + "-" + (index + 1) + extensionFor(matcher.group(1)));
            item.setFileSize((long) bytes.length);
            item.setDescription(description);
            item.setUploadedBy(uploadedBy);
            item.setOrganization(uploadedBy.getOrganization());
            media.add(item);
        }
        return media;
    }

    private Matcher matcher(String image) {
        if (image == null) {
            throw new InvalidFileException("A imagem enviada possui formato inválido.");
        }
        Matcher matcher = IMAGE_DATA_URL.matcher(image);
        if (!matcher.matches()) {
            throw new InvalidFileException("A imagem enviada possui formato inválido.");
        }
        return matcher;
    }

    private byte[] decodedBytes(String base64) {
        try {
            return Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException exception) {
            throw new InvalidFileException("A imagem enviada possui Base64 inválido.", exception);
        }
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> ".svg";
        };
    }
}
