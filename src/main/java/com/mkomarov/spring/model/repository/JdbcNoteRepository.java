package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Note;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
@Profile("jdbc")
public class JdbcNoteRepository implements NoteRepository {

    private final DataSource dataSource;

    public JdbcNoteRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Note> getById(UUID id) {
        String sql = "SELECT * FROM notes WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToNote(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching note by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Set<Note> getAll(String title, int limit, int offset) {
        StringBuilder sql = new StringBuilder("SELECT * FROM notes WHERE 1=1");
        if (title != null && !title.isBlank()) {
            sql.append(" AND title LIKE ?");
        }
        sql.append(" ORDER BY id ASC LIMIT ? OFFSET ?");

        Set<Note> notes = new HashSet<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (title != null && !title.isBlank()) {
                ps.setString(paramIndex++, "%" + title + "%");
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapRowToNote(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all notes", e);
        }
        return notes;
    }

    @Override
    public Note create(Note note) {
        UUID id = Objects.isNull(note.id()) ? UUID.randomUUID() : note.id();
        String sql = "INSERT INTO notes (id, title, content) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            ps.setString(2, note.title());
            ps.setString(3, note.content());
            ps.executeUpdate();

            return new Note(id, note.title(), note.content());
        } catch (SQLException e) {
            throw new RuntimeException("Error creating note", e);
        }
    }

    @Override
    public boolean update(Note note) {
        String sql = "UPDATE notes SET title = ?, content = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, note.title());
            ps.setString(2, note.content());
            ps.setObject(3, note.id());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating note", e);
        }
    }

    @Override
    public Optional<Note> delete(UUID id) {
        Optional<Note> noteToDelete = getById(id);
        if (noteToDelete.isEmpty()) return Optional.empty();

        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            ps.executeUpdate();
            return noteToDelete;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting note", e);
        }
    }

    @Override
    public long count(String title) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM notes WHERE 1=1");
        boolean hasTitle = title != null && !title.isBlank();
        if (hasTitle) {
            sql.append(" AND title LIKE ?");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (hasTitle) {
                ps.setString(1, "%" + title + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting notes", e);
        }
        return 0L;
    }

    @Override
    public Optional<Note> getByTitle(String title) {
        String sql = "SELECT * FROM notes WHERE title = ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToNote(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching note by title", e);
        }
        return Optional.empty();
    }

    private Note mapRowToNote(ResultSet rs) throws SQLException {
        return new Note(
                (UUID) rs.getObject("id"),
                rs.getString("title"),
                rs.getString("content")
        );
    }
}