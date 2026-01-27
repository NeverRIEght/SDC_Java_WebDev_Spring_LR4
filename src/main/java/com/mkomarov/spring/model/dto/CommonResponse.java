package com.mkomarov.spring.model.dto;

import java.time.Instant;

public record CommonResponse<T>(
        T data,
        String error,
        String timestamp
) {
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(data, null, Instant.now().toString());
    }

    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(null, message, Instant.now().toString());
    }
}
