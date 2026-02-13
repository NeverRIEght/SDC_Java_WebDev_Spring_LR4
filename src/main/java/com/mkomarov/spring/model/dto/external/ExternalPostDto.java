package com.mkomarov.spring.model.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalPostDto implements Serializable {
    private Long userId;
    private Long id;
    private String title;
    private String body;
}

