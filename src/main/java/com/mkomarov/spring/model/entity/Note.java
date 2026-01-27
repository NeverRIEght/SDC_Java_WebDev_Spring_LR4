package com.mkomarov.spring.model.entity;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Note(UUID id, String title, String content) {
}
