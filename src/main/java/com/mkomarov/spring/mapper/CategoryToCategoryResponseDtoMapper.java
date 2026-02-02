package com.mkomarov.spring.mapper;

import com.mkomarov.spring.model.dto.response.CategoryResponseDto;
import com.mkomarov.spring.model.dto.response.NoteResponseDto;
import com.mkomarov.spring.model.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryToCategoryResponseDtoMapper implements AbstractMapper<Category, CategoryResponseDto> {
    private final NoteToNoteResponseDtoMapper noteDtoMapper;

    public CategoryToCategoryResponseDtoMapper(NoteToNoteResponseDtoMapper noteDtoMapper) {
        this.noteDtoMapper = noteDtoMapper;
    }

    @Override
    public CategoryResponseDto map(Category category) {
        if (category == null) {
            return null;
        }

        List<NoteResponseDto> notes;
        if (category.getNotes() == null) {
            notes = null;
        } else {
            notes = category.getNotes().stream()
                    .map(noteDtoMapper::map)
                    .toList();
        }

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .notes(notes)
                .build();
    }
}
