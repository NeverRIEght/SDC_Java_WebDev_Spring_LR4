package com.mkomarov.spring.client;

import com.mkomarov.spring.exception.classes.ExternalApiException;
import com.mkomarov.spring.model.dto.external.ExternalPostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalApiClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiClient.class);
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private final RestClient restClient;

    public ExternalApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public ExternalPostDto fetchPost(Long postId) {
        log.info("Fetching post with ID {} from external API", postId);

        try {
            ExternalPostDto response = restClient.get()
                    .uri("/posts/{id}", postId)
                    .retrieve()
                    .body(ExternalPostDto.class);

            if (response == null) {
                throw new ExternalApiException("Received null response from external API");
            }

            log.info("Successfully fetched post: {}", response.getTitle());
            return response;

        } catch (RestClientException e) {
            log.error("Failed to fetch post from external API: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch data from external API", e);
        }
    }

    public ExternalPostDto fetchRandomPost() {
        Long randomId = (long) (Math.random() * 100) + 1;
        return fetchPost(randomId);
    }
}


