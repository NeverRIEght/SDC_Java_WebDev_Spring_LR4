package com.mkomarov.spring.listener;

import com.mkomarov.spring.config.RabbitMQConfig;
import com.mkomarov.spring.exception.classes.ResourceNotFoundException;
import com.mkomarov.spring.model.dto.message.EnrichmentMessage;
import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.model.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EnrichmentMessageListener {

    private static final Logger log = LoggerFactory.getLogger(EnrichmentMessageListener.class);

    private final NoteRepository noteRepository;

    public EnrichmentMessageListener(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleEnrichmentMessage(EnrichmentMessage message) {
        log.info("-----");
        log.info("Received enrichment message from queue");
        log.info("Note ID: {}", message.getNoteId());
        log.info("Source: {}", message.getEnrichmentSource());
        log.info("Timestamp: {}", message.getTimestamp());
        log.info("Correlation ID: {}", message.getCorrelationId());
        log.info("-----");

        try {
            processEnrichment(message);

            log.info("Successfully processed enrichment message. CorrelationId: {}",
                    message.getCorrelationId());

        } catch (ResourceNotFoundException e) {
            log.error("Note not found - sending to DLQ. CorrelationId: {}, Error: {}",
                    message.getCorrelationId(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Note not found - cannot process", e);
        } catch (Exception e) {
            log.error("Failed to process enrichment message. CorrelationId: {}, Error: {}",
                    message.getCorrelationId(), e.getMessage(), e);
            // Retry, then send to DLQ
            throw new RuntimeException("Failed to process enrichment", e);
        }
    }

    private void processEnrichment(EnrichmentMessage message) {
        log.info("Processing enrichment for note ID: {}", message.getNoteId());

        Note note = noteRepository.getById(message.getNoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Note not found: " + message.getNoteId()));

        log.info("Found note: {}", note.getTitle());

        String updatedContent = buildUpdatedContent(note.getContent(), message);
        note.setContent(updatedContent);

        noteRepository.update(note);

        log.info("Successfully updated note with enrichment data. Note ID: {}", message.getNoteId());

        log.info("-----");
        log.info("Enrichment Details:");
        log.info("Data:\n{}", message.getEnrichmentData());
        log.info("-----");
    }

    private String buildUpdatedContent(String currentContent, EnrichmentMessage message) {
        StringBuilder content = new StringBuilder();

        if (currentContent != null && !currentContent.isEmpty()) {
            content.append(currentContent);
            content.append("\n\n");
        }

        content.append("----- Enrichment Data -----\n");
        content.append("Source: ").append(message.getEnrichmentSource()).append("\n");
        content.append("Added at: ").append(message.getTimestamp()).append("\n");
        content.append("Correlation ID: ").append(message.getCorrelationId()).append("\n\n");
        content.append(message.getEnrichmentData());

        return content.toString();
    }
}

