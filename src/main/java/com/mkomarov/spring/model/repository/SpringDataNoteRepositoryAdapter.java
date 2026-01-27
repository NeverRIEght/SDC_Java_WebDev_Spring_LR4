package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Note;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("springdata")
public class SpringDataNoteRepositoryAdapter implements NoteRepository {
    private final SpringDataNoteRepository repository;

    public SpringDataNoteRepositoryAdapter(SpringDataNoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Note> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Set<Note> getAll(String title, int limit, int offset) {
        int pageNumber = offset / limit;
        PageRequest pageRequest = PageRequest.of(pageNumber, limit, Sort.by("id").ascending());

        return repository.findAll(pageRequest).stream().collect(Collectors.toSet());
    }

    @Override
    public Note create(Note note) {
        return repository.save(note);
    }

    @Override
    public boolean update(Note note) {
        if (repository.existsById(note.id())) {
            repository.save(note);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Note> delete(UUID id) {
        return repository.findById(id).map(note -> {
            repository.deleteById(id);
            return note;
        });
    }

    @Override
    public long count(String title) {
        if (title != null && !title.isBlank()) {
            return repository.countByTitleContaining(title);
        }
        return repository.count();
    }

    @Override
    public Optional<Note> getByTitle(String title) {
        return repository.findByTitle(title);
    }
}
