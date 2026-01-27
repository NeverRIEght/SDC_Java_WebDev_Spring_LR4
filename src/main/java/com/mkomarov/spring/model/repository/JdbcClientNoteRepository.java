package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Note;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Profile("jdbcclient")
public class JdbcClientNoteRepository implements NoteRepository {

    private final JdbcClient jdbcClient;

    public JdbcClientNoteRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Note> getById(UUID id) {
        return jdbcClient.sql("SELECT * FROM notes WHERE id = :id")
                .params(Map.of("id", id))
                .query(this::mapRow)
                .optional();
    }

    @Override
    public Set<Note> getAll(String title, int limit, int offset) {
        StringBuilder sql = new StringBuilder("SELECT * FROM notes WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        params.put("limit", limit);
        params.put("offset", offset);

        if (title != null && !title.isBlank()) {
            sql.append(" AND title LIKE :title");
            params.put("title", "%" + title + "%");
        }
        sql.append(" ORDER BY id ASC LIMIT :limit OFFSET :offset");

        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(this::mapRow)
                .stream()
                .collect(Collectors.toSet());
    }

    @Override
    public Note create(Note note) {
        UUID id = Objects.isNull(note.id()) ? UUID.randomUUID() : note.id();

        jdbcClient.sql("INSERT INTO notes (id, title, content) VALUES (:id, :title, :content)")
                .params(Map.of(
                        "id", id,
                        "title", note.title(),
                        "content", note.content()
                ))
                .update();

        return new Note(id, note.title(), note.content());
    }

    @Override
    public boolean update(Note note) {
        int updated = jdbcClient.sql("UPDATE notes SET title = :title, content = :content WHERE id = :id")
                .params(Map.of(
                        "title", note.title(),
                        "content", note.content(),
                        "id", note.id()
                ))
                .update();

        return updated > 0;
    }

    @Override
    public Optional<Note> delete(UUID id) {
        return getById(id).map(note -> {
            jdbcClient.sql("DELETE FROM notes WHERE id = :id")
                    .params(Map.of("id", id))
                    .update();
            return note;
        });
    }

    @Override
    public long count(String title) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM notes WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (title != null && !title.isBlank()) {
            sql.append(" AND title LIKE :title");
            params.put("title", "%" + title + "%");
        }

        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(Long.class)
                .single();
    }

    @Override
    public Optional<Note> getByTitle(String title) {
        return jdbcClient.sql("SELECT * FROM notes WHERE title = :title LIMIT 1")
                .params(Map.of("title", title))
                .query(this::mapRow)
                .optional();
    }

    private Note mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Note(
                (UUID) rs.getObject("id"),
                rs.getString("title"),
                rs.getString("content")
        );
    }
}