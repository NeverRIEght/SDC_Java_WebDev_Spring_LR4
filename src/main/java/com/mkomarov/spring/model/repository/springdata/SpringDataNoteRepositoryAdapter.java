package com.mkomarov.spring.model.repository.springdata;

import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.model.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@Profile("spring-data")
@RequiredArgsConstructor
public class SpringDataNoteRepositoryAdapter implements NoteRepository {
    private final SpringDataNoteRepository repository;

    @Override
    public Optional<Note> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Set<Note> getAll(String title, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return repository.findAllByTitle(title, pageable).toSet();
    }

    @Override
    public Note create(Note note) {
        return repository.save(note);
    }

    @Override
    public boolean update(Note note) {
        if (repository.existsById(note.getId())) {
            repository.save(note);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Note> delete(UUID id) {
        return repository.findById(id).map(note -> {
            repository.delete(note);
            return note;
        });
    }

    @Override
    public long count(String title) {
        return repository.count();
    }

    @Override
    public Optional<Note> getByTitle(String title) {
        return repository.findByTitle(title);
    }
}
