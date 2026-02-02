package com.mkomarov.spring.service;

import com.mkomarov.spring.exception.classes.*;
import com.mkomarov.spring.mapper.NoteToNoteResponseDtoMapper;
import com.mkomarov.spring.model.dto.CommonResponse;
import com.mkomarov.spring.model.dto.request.NoteCreateRequest;
import com.mkomarov.spring.model.dto.PaginatedResponse;
import com.mkomarov.spring.model.dto.request.NoteUpdateRequest;
import com.mkomarov.spring.model.dto.response.NoteResponseDto;
import com.mkomarov.spring.model.entity.Category;
import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.model.repository.CategoryRepository;
import com.mkomarov.spring.model.repository.NoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class NoteService {
    private static final int PAGINATION_MAX_SIZE = 100;

    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;
    private final NoteToNoteResponseDtoMapper noteToDtoMapper;

    public NoteService(NoteRepository noteRepository,
                       CategoryRepository categoryRepository,
                       NoteToNoteResponseDtoMapper noteToDtoMapper) {
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
        this.noteToDtoMapper = noteToDtoMapper;
    }

    public CommonResponse<NoteResponseDto> getById(UUID id) {
        if (id == null) {
            throw new InvalidRequestException("Id must not be null");
        }

        Note note = noteRepository.getById(id).orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        NoteResponseDto responseDto = noteToDtoMapper.map(note);

        return CommonResponse.success(responseDto);
    }

    public PaginatedResponse<NoteResponseDto> getAll(String title, Integer page, Integer size) {
        if (page == null || size == null) {
            throw new InvalidPaginationException("Page and size must be present", page, size);
        }

        if (page < 0 || size <= 0) {
            throw new InvalidPaginationException("Page and size must be positive", page, size);
        }

        if (size > PAGINATION_MAX_SIZE) {
            size = PAGINATION_MAX_SIZE;
        }

        int offset = page * size;
        Set<Note> notes = noteRepository.getAll(title, size, offset);
        long totalElements = noteRepository.count(title);

        List<NoteResponseDto> responseDtos = notes.stream()
                .map(noteToDtoMapper::map)
                .toList();

        return PaginatedResponse.success(responseDtos, page, size, totalElements);
    }

    @Transactional
    public CommonResponse<NoteResponseDto> create(NoteCreateRequest request) {
        if (noteRepository.getByTitle(request.title()).isPresent()) {
            throw new ResourceAlreadyExistsException("Note with this title already exists");
        }

        Category category = getCategory(request.categoryId());

        Note note = new Note();
        note.setTitle(request.title());
        note.setContent(request.content());
        note.setCategory(category);

        Note savedNote = noteRepository.create(note);

        NoteResponseDto responseDto = noteToDtoMapper.map(savedNote);

        return CommonResponse.success(responseDto);
    }

    @Transactional
    public CommonResponse<NoteResponseDto> update(UUID id, NoteCreateRequest request) {
        NoteUpdateRequest noteUpdateRequest = NoteUpdateRequest.builder()
                .id(id)
                .title(request.title())
                .content(request.content())
                .categoryId(request.categoryId())
                .build();

        return this.update(noteUpdateRequest);
    }

    @Transactional
    public CommonResponse<NoteResponseDto> update(NoteUpdateRequest request) {
        if (request.id() == null) {
            throw new InvalidRequestException("Id must not be null");
        }

        Note note = noteRepository.getById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("No Note found with id: " + request.id()));

        note.setTitle(request.title());
        note.setContent(request.content());
        note.setCategory(getCategory(request.categoryId()));

        noteRepository.update(note);

        return CommonResponse.success(noteToDtoMapper.map(note));
    }

    public CommonResponse<NoteResponseDto> delete(UUID id) {
        return noteRepository.delete(id)
                .map(noteToDtoMapper::map)
                .map(CommonResponse::success)
                .orElseThrow(() -> new ResourceNotFoundException("No Note with such id found"));
    }

    private Category getCategory(UUID id) {
        Optional<Category> existingCategory = categoryRepository.getById(id);
        return existingCategory.orElse(null);
    }


}
