package com.mkomarov.spring.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CategoryCreateRequest(
        @NotBlank(message = "Name must not be empty")
        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name,

        List<UUID> notesIdsToAdd) {
}
