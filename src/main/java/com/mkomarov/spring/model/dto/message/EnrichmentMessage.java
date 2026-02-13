package com.mkomarov.spring.model.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrichmentMessage implements Serializable {
    private UUID noteId;
    private String enrichmentSource;
    private String enrichmentData;
    private LocalDateTime timestamp;
    private String correlationId;
}

