package com.mkomarov.spring.model.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Collection;

@Builder
public record PaginatedResponse<T>(
        Collection<T> data,
        String error,
        Integer page,
        Integer pageSize,
        long totalElements,
        String timestamp
) {
    public static <T> PaginatedResponse<T> success(Collection<T> data, Integer page, Integer pageSize, long totalElements) {
        return new PaginatedResponse<>(data, null, page, pageSize, totalElements, Instant.now().toString());
    }

    public static <T> PaginatedResponse<T> error(String message, Integer page, Integer pageSize) {
        return new PaginatedResponse<>(null, message, page, pageSize, 0, Instant.now().toString());
    }
}