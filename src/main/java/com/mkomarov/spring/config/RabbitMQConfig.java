package com.mkomarov.spring.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "note.enrichment.queue";
    public static final String EXCHANGE_NAME = "note.enrichment.exchange";
    public static final String ROUTING_KEY = "note.enrichment.routing.key";
    public static final String DLQ_NAME = "note.enrichment.dlq";
    public static final String DLQ_EXCHANGE_NAME = "note.enrichment.dlq.exchange";
    public static final String DLQ_ROUTING_KEY = "note.enrichment.dlq.routing.key";

    @Bean
    public Queue enrichmentQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ_NAME, true);
    }

    @Bean
    public TopicExchange enrichmentExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DLQ_EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue enrichmentQueue, TopicExchange enrichmentExchange) {
        return BindingBuilder
                .bind(enrichmentQueue)
                .to(enrichmentExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}

