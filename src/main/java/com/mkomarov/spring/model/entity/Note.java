package com.mkomarov.spring.model.entity;

import jakarta.persistence.Table;
import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Builder
@Table(name = "notes")
public record Note(
        @Id UUID id,
        String title,
        String content) {
}
