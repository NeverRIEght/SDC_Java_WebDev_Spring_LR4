package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Note;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("jdbctemplate")
public class JdbcTemplateNoteRepository implements NoteRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateNoteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Note> getById(UUID id) {
        return jdbcTemplate.query(
                "SELECT * FROM notes WHERE id = ?",
                (rs, rowNum) -> new Note(
                        (UUID) rs.getObject("id"),
                        rs.getString("title"),
                        rs.getString("content")
                ),
                id
        ).stream().findFirst();
    }

    @Override
    public Set<Note> getAll(String title, int limit, int offset) {
        StringBuilder sql = new StringBuilder("SELECT * FROM notes WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title + "%");
        }

        sql.append(" ORDER BY id ASC");
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return new HashSet<>(jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> new Note(
                        (UUID) rs.getObject("id"),
                        rs.getString("title"),
                        rs.getString("content")
                ),
                params.toArray()
        ));
    }

    @Override
    public Note create(Note note) {
        UUID id = Objects.isNull(note.id()) ? UUID.randomUUID() : note.id();
        jdbcTemplate.update(
                "INSERT INTO notes (id, title, content) VALUES (?, ?, ?)",
                id, note.title(), note.content()
        );
        return new Note(id, note.title(), note.content());
    }

    @Override
    public boolean update(Note note) {
        int rowsAffected = jdbcTemplate.update(
                "UPDATE notes SET title = ?, content = ? WHERE id = ?",
                note.title(), note.content(), note.id()
        );
        return rowsAffected > 0;
    }

    @Override
    public Optional<Note> delete(UUID id) {
        Optional<Note> noteToDelete = getById(id);

        if (noteToDelete.isEmpty()) {
            return Optional.empty();
        }

        jdbcTemplate.update("DELETE FROM notes WHERE id = ?", id);

        return noteToDelete;
    }

    public long count(String title) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM notes WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title + "%");
        }

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray()))
                .orElse(0L);
    }

    public Optional<Note> getByTitle(String title) {
        String sql = "SELECT * FROM notes WHERE title = ? LIMIT 1";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Note(
                        (UUID) rs.getObject("id"),
                        rs.getString("title"),
                        rs.getString("content")
                ),
                title
        ).stream().findFirst();
    }
}
