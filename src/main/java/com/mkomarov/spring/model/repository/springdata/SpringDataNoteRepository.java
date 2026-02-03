package com.mkomarov.spring.model.repository.springdata;

import com.mkomarov.spring.model.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataNoteRepository extends JpaRepository<Note, UUID> {
    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.category " +
            "WHERE (:title IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    Page<Note> findAllByTitle(@Param("title") String title, Pageable pageable);

    Optional<Note> findByTitle(String title);
}
