package com.mkomarov.spring.service;

import com.mkomarov.spring.client.ExternalApiClient;
import com.mkomarov.spring.exception.classes.ResourceNotFoundException;
import com.mkomarov.spring.model.dto.external.ExternalPostDto;
import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.model.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EnrichmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrichmentService.class);

    private final NoteRepository noteRepository;
    private final ExternalApiClient externalApiClient;
    private final MessageProducerService messageProducerService;

    public EnrichmentService(NoteRepository noteRepository, ExternalApiClient externalApiClient, MessageProducerService messageProducerService) {
        this.noteRepository = noteRepository;
        this.externalApiClient = externalApiClient;
        this.messageProducerService = messageProducerService;
    }

    public String enrichNoteWithExternalData(UUID noteId) {
        log.info("Starting enrichment process for note ID: {}", noteId);

        Note note = noteRepository.getById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + noteId));

        log.info("Note found: {}", note.getTitle());

        ExternalPostDto externalPost = externalApiClient.fetchRandomPost();

        log.info("Fetched external post: {} (ID: {})", externalPost.getTitle(), externalPost.getId());

        String enrichmentData = formatEnrichmentData(externalPost);
        String correlationId = messageProducerService.sendEnrichment(
                noteId,
                enrichmentData,
                "JSONPlaceholder"
        );

        log.info("Enrichment process initiated. CorrelationId: {}", correlationId);

        return correlationId;
    }

    private String formatEnrichmentData(ExternalPostDto post) {
        return String.format("""
                                External Post (ID: %d):\
                                Title: %s\
                                Body: %s\
                                User ID: %d
                        """,
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getUserId());
    }
}

