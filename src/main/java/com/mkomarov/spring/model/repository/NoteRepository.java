package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository {
    Optional<Note> getById(UUID id);

    List<Note> getAll(String title, int limit, int offset);
    Note create(Note note);
    boolean update(Note note);
    Optional<Note> delete(UUID id);
    long count(String title);
    Optional<Note> getByTitle(String title);
}
