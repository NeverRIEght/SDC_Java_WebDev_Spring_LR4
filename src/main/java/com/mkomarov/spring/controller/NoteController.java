package com.mkomarov.spring.controller;

import com.mkomarov.spring.model.dto.CommonResponse;
import com.mkomarov.spring.model.dto.PaginatedResponse;
import com.mkomarov.spring.model.dto.request.NoteCreateRequest;
import com.mkomarov.spring.model.dto.request.NoteUpdateRequest;
import com.mkomarov.spring.model.dto.response.NoteResponseDto;
import com.mkomarov.spring.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequestMapping(value = "/api/notes")
@Validated
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<NoteResponseDto>> getAll(
            @RequestParam(value = "nameFilter", required = false)
            String nameToFilterBy,
            @RequestParam(value = "page", defaultValue = "0", required = false)
            @Min(0)
            Integer page,
            @RequestParam(value = "size", defaultValue = "2", required = false)
            @Min(0)
            @Max(100)
            Integer size,
            @RequestParam(value = "sortBy", defaultValue = "title")
            String sortBy,
            @RequestParam(value = "direction", defaultValue = "ASC")
            String direction
    ) {
        return ResponseEntity.ok(noteService.getAll(nameToFilterBy, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<NoteResponseDto>> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(noteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<NoteResponseDto>> create(@Valid @RequestBody NoteCreateRequest request) {
        return ResponseEntity.ok(noteService.create(request));
    }

    @PutMapping()
    public ResponseEntity<CommonResponse<NoteResponseDto>> update(
            @Valid @RequestBody NoteUpdateRequest request
    ) {
        return ResponseEntity.ok(noteService.update(request));
    }

    @Deprecated
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<NoteResponseDto>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody NoteCreateRequest request
    ) {
        return ResponseEntity.ok(noteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<NoteResponseDto>> delete(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(noteService.delete(id));
    }
}
