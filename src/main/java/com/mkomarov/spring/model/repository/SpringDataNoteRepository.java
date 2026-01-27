package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Note;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataNoteRepository extends ListCrudRepository<Note, UUID>, PagingAndSortingRepository<Note, UUID> {
    Optional<Note> findByTitle(String title);
    long countByTitleContaining(String title);
}
