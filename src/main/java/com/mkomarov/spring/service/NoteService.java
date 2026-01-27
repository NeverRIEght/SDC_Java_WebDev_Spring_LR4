package com.mkomarov.spring.service;

import com.mkomarov.spring.exception.classes.*;
import com.mkomarov.spring.model.dto.CommonResponse;
import com.mkomarov.spring.model.dto.NoteRequest;
import com.mkomarov.spring.model.dto.PaginatedResponse;
import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.model.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public CommonResponse<Note> getById(UUID id) {
        if (id == null) {
            throw new InvalidRequestException("Id must not be null");
        }

        return noteRepository.getById(id)
                .map(CommonResponse::success)
                .orElseThrow(() -> new ResourceNotFoundException("No Note with such id found"));
    }

    public PaginatedResponse<Note> getAll(String title, Integer page, Integer size) {
        if (page == null || size == null) {
            throw new InvalidPaginationException("Page and size must be present", page, size);
        }

        if (page < 0 || size <= 0) {
            throw new InvalidPaginationException("Page and size must be positive", page, size);
        }

        if (size > 100) {
            size = 100;
        }

        int offset = page * size;
        Set<Note> notes = noteRepository.getAll(title, size, offset);
        long totalElements = noteRepository.count(title);

        return PaginatedResponse.success(notes, page, size, totalElements);
    }

    public CommonResponse<Note> create(NoteRequest request) {
        if (noteRepository.getByTitle(request.title()).isPresent()) {
            throw new ResourceAlreadyExistsException("Note with this title already exists");
        }

        Note savedNote = noteRepository.create(Note.builder()
                .id(UUID.randomUUID())
                .title(request.title())
                .content(request.content())
                .build());

        return CommonResponse.success(savedNote);
    }

    public CommonResponse<Note> update(UUID id, NoteRequest request) {
        if (id == null) {
            throw new InvalidRequestException("Id must not be null");
        }

        Note noteToUpdate = Note.builder()
                .id(id)
                .title(request.title())
                .content(request.content())
                .build();

        boolean result = noteRepository.update(noteToUpdate);

        if (!result) {
            throw new ResourceNotFoundException("No Note with such id found");
        }

        return CommonResponse.success(noteToUpdate);
    }

    public CommonResponse<Note> delete(UUID id) {
        if (Math.random() < 0.5) {
            throw new ServiceUnavailableException("Service unavailable. Try again.");
        }

        return noteRepository.delete(id)
                .map(CommonResponse::success)
                .orElseThrow(() -> new ResourceNotFoundException("No Note with such id found"));
    }
}
