package com.mkomarov.spring.service;

import com.mkomarov.spring.config.RabbitMQConfig;
import com.mkomarov.spring.model.dto.message.EnrichmentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MessageProducerService {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    private final RabbitTemplate rabbitTemplate;

    public MessageProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEnrichmentMessage(EnrichmentMessage message) {
        log.info("Sending enrichment message to queue. NoteId: {}, Source: {}, CorrelationId: {}",
                message.getNoteId(), message.getEnrichmentSource(), message.getCorrelationId());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    message
            );

            log.info("Successfully sent message to queue. CorrelationId: {}", message.getCorrelationId());

        } catch (Exception e) {
            log.error("Failed to send message to queue. CorrelationId: {}, Error: {}",
                    message.getCorrelationId(), e.getMessage());
            throw new RuntimeException("Failed to send message to RabbitMQ", e);
        }
    }

    public String sendEnrichment(UUID noteId, String enrichmentData, String source) {
        String correlationId = UUID.randomUUID().toString();

        EnrichmentMessage message = EnrichmentMessage.builder()
                .noteId(noteId)
                .enrichmentData(enrichmentData)
                .enrichmentSource(source)
                .timestamp(java.time.LocalDateTime.now())
                .correlationId(correlationId)
                .build();

        sendEnrichmentMessage(message);
        return correlationId;
    }
}

