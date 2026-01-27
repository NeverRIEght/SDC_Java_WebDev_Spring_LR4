package com.mkomarov.spring.controller;

import com.mkomarov.spring.model.dto.CommonResponse;
import com.mkomarov.spring.model.dto.NoteRequest;
import com.mkomarov.spring.model.dto.PaginatedResponse;
import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PaginatedResponse<Note>> getAll(
            @RequestParam(value = "nameFilter", required = false)
            String nameToFilterBy,
            @RequestParam(value = "page", defaultValue = "0", required = false)
            @Min(0)
            Integer page,
            @RequestParam(value = "size", defaultValue = "2", required = false)
            @Min(0)
            @Max(100)
            Integer size
    ) {
        return ResponseEntity.ok(noteService.getAll(nameToFilterBy, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Note>> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(noteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<Note>> create(@Valid @RequestBody NoteRequest request) {
        return ResponseEntity.ok(noteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<Note>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody NoteRequest request
    ) {
        return ResponseEntity.ok(noteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Note>> delete(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(noteService.delete(id));
    }
}
