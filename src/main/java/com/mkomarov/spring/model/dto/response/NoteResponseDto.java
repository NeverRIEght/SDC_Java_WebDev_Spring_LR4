package com.mkomarov.spring.model.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record NoteResponseDto(
        UUID id,
        String title,
        String content,
        UUID categoryId,
        String categoryName) {
}
