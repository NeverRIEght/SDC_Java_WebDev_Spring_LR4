package com.mkomarov.spring.mapper;

import com.mkomarov.spring.model.dto.response.NoteResponseDto;
import com.mkomarov.spring.model.entity.Category;
import com.mkomarov.spring.model.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteToNoteResponseDtoMapper implements AbstractMapper<Note, NoteResponseDto> {
    @Override
    public NoteResponseDto map(Note note) {
        Category category = note.getCategory();

        return NoteResponseDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .build();
    }
}
