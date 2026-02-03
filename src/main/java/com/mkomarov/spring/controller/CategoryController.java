package com.mkomarov.spring.controller;

import com.mkomarov.spring.model.dto.CommonResponse;
import com.mkomarov.spring.model.dto.PaginatedResponse;
import com.mkomarov.spring.model.dto.request.CategoryCreateRequest;
import com.mkomarov.spring.model.dto.request.CategoryUpdateRequest;
import com.mkomarov.spring.model.dto.response.CategoryResponseDto;
import com.mkomarov.spring.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<CategoryResponseDto>> getAll(
            @RequestParam(required = false)
            String nameFilter,
            @RequestParam(defaultValue = "0")
            @Min(0)
            Integer page,
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            Integer size,
            @RequestParam(value = "sortBy", defaultValue = "title")
            String sortBy,
            @RequestParam(value = "direction", defaultValue = "ASC")
            String direction
    ) {
        return ResponseEntity.ok(categoryService.getAll(nameFilter, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<CategoryResponseDto>> create(
            @Valid @RequestBody CategoryCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        if (!id.equals(request.id())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(categoryService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponseDto>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.delete(id));
    }
}