package com.weg.Maintenance_API.buy.dto.response;

import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.media.dto.response.MediaResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record BuyDtoResponse(
        Long id,
        String numberCard,
        BuyStatus status,
        Long createdById,
        String createdByName,
        Long notifiedTeacherId,
        String notifiedTeacherName,
        String purchaseJustification,
        Long classGroupId,
        String classGroupAcronym,
        LocalDateTime createdAt,
        List<BuyItemResponse> items,
        List<MediaResponseDto> media
) {
}
