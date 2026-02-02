package com.mkomarov.spring.model.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CategoryResponseDto(
        UUID id,
        String name,
        List<NoteResponseDto> notes) {
}
