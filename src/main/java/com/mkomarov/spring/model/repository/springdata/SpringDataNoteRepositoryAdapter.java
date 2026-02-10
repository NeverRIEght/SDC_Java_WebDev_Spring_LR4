package com.mkomarov.spring.model.repository.springdata;

import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.model.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpringDataNoteRepositoryAdapter implements NoteRepository {
    private final SpringDataNoteRepository repository;

    @Override
    public Optional<Note> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Note> getAll(String title, int limit, int offset, String sortBy, String direction) {
        int pageNumber = offset / limit;

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, limit, sort);

        return repository.findAllByTitle(title, pageable).toList();
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
