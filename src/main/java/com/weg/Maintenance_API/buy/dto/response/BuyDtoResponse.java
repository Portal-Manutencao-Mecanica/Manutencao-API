package com.weg.Maintenance_API.buy.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.media.dto.response.MediaResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record BuyDtoResponse(
        UUID id,
        String numberCard,
        BuyStatus status,
        UUID createdById,
        String createdByName,
        UUID notifiedTeacherId,
        String notifiedTeacherName,
        String purchaseJustification,
        UUID classGroupId,
        String classGroupAcronym,
        LocalDateTime createdAt,
        List<BuyItemResponse> items,
        List<MediaResponseDto> media
) {
}
