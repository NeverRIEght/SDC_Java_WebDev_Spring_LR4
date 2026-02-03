package com.mkomarov.spring.service;

import com.mkomarov.spring.exception.classes.InvalidPaginationException;
import com.mkomarov.spring.exception.classes.ResourceAlreadyExistsException;
import com.mkomarov.spring.exception.classes.ResourceConflictException;
import com.mkomarov.spring.exception.classes.ResourceNotFoundException;
import com.mkomarov.spring.mapper.CategoryToCategoryResponseDtoMapper;
import com.mkomarov.spring.model.dto.CommonResponse;
import com.mkomarov.spring.model.dto.PaginatedResponse;
import com.mkomarov.spring.model.dto.request.CategoryCreateRequest;
import com.mkomarov.spring.model.dto.request.CategoryUpdateRequest;
import com.mkomarov.spring.model.dto.response.CategoryResponseDto;
import com.mkomarov.spring.model.entity.Category;
import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.model.repository.CategoryRepository;
import com.mkomarov.spring.model.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private static final int PAGINATION_MAX_SIZE = 100;

    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;
    private final CategoryToCategoryResponseDtoMapper categoryMapper;

    public CommonResponse<CategoryResponseDto> getById(UUID id) {
        Category category = categoryRepository.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return CommonResponse.success(categoryMapper.map(category));
    }

    public PaginatedResponse<CategoryResponseDto> getAll(String name,
                                                         Integer page,
                                                         Integer size,
                                                         String sortBy,
                                                         String direction) {
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
        List<Category> categories = categoryRepository.getAll(name, size, offset, sortBy, direction);
        long totalElements = categoryRepository.count(name);

        List<CategoryResponseDto> responseDtos = categories.stream()
                .map(categoryMapper::map)
                .toList();

        return PaginatedResponse.success(responseDtos, page, size, totalElements);
    }

    @Transactional
    public CommonResponse<CategoryResponseDto> create(CategoryCreateRequest request) {
        categoryRepository.getByName(request.name())
                .ifPresent(_ -> {
                    throw new ResourceAlreadyExistsException("Category name already taken");
                });

        Category category = new Category();
        category.setName(request.name());
        Category savedCategory = categoryRepository.create(category);

        if (request.notesIdsToAdd() != null && !request.notesIdsToAdd().isEmpty()) {
            linkNotesToCategory(savedCategory, request.notesIdsToAdd());
        }

        return CommonResponse.success(categoryMapper.map(savedCategory));
    }

    @Transactional
    public CommonResponse<CategoryResponseDto> update(CategoryUpdateRequest request) {
        Category category = categoryRepository.getById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getName().equals(request.name())) {
            categoryRepository.getByName(request.name())
                    .ifPresent(_ -> {
                        throw new ResourceAlreadyExistsException("Name already taken");
                    });
        }

        category.setName(request.name());
        categoryRepository.update(category);

        return CommonResponse.success(categoryMapper.map(category));
    }

    @Transactional
    public CommonResponse<CategoryResponseDto> delete(UUID id) {
        Category category = categoryRepository.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (category.getNotes() != null) {
            category.getNotes().forEach(note -> {
                note.setCategory(null);
                noteRepository.update(note);
            });
        }

        categoryRepository.delete(id);
        return CommonResponse.success(categoryMapper.map(category));
    }

    private void linkNotesToCategory(Category category, List<UUID> noteIds) {
        for (UUID noteId : noteIds) {
            Note note = noteRepository.getById(noteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Note not found: " + noteId));

            if (note.getCategory() != null && !note.getCategory().getId().equals(category.getId())) {
                throw new ResourceConflictException(
                        "Note \"" + note.getTitle() + "\" is already assigned to category: " + note.getCategory().getName()
                );
            }

            note.setCategory(category);
            noteRepository.update(note);
        }
    }
}