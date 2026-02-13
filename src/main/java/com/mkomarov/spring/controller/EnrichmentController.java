package com.mkomarov.spring.controller;

import com.mkomarov.spring.model.dto.CommonResponse;
import com.mkomarov.spring.service.EnrichmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrichment")
public class EnrichmentController {

    private final EnrichmentService enrichmentService;

    public EnrichmentController(EnrichmentService enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @PostMapping("/notes/{noteId}")
    public ResponseEntity<CommonResponse<Map<String, String>>> enrichNote(
            @PathVariable UUID noteId) {

        String correlationId = enrichmentService.enrichNoteWithExternalData(noteId);

        Map<String, String> responseData = Map.of(
                "status", "processing",
                "correlationId", correlationId,
                "message", "Async Note enrichment initiated."
        );

        return ResponseEntity
                .accepted()
                .body(CommonResponse.success(responseData));
    }
}

