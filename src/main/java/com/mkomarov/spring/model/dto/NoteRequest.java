package com.mkomarov.spring.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record NoteRequest(
        @NotBlank(message = "Title must not be empty")
        @Size(min = 1, max = 20, message = "Title must be between 1 and 20 characters")
        String title,

        @NotBlank(message = "Content must not be empty")
        @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
        String content) {
}
