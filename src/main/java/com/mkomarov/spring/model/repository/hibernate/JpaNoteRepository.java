package com.mkomarov.spring.model.repository.hibernate;

import com.mkomarov.spring.model.entity.Note;
import com.mkomarov.spring.model.repository.NoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class JpaNoteRepository implements NoteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Note> getById(UUID id) {
        return Optional.ofNullable(entityManager.find(Note.class, id));
    }

    @Override
    @Transactional
    public Note create(Note note) {
        entityManager.persist(note);
        return note;
    }

    @Override
    @Transactional
    public boolean update(Note note) {
        if (entityManager.find(Note.class, note.getId()) == null) {
            return false;
        }
        entityManager.merge(note);
        return true;
    }

    @Override
    @Transactional
    public Optional<Note> delete(UUID id) {
        return getById(id).map(note -> {
            entityManager.remove(note);
            return note;
        });
    }

    @Override
    public List<Note> getAll(String title, int limit, int offset, String sortBy, String direction) {
        String jpql = "SELECT n FROM Note n LEFT JOIN FETCH n.category " +
                "WHERE :title IS NULL OR n.title LIKE :title";

        TypedQuery<Note> query = entityManager.createQuery(jpql, Note.class)
                .setParameter("title", title != null ? "%" + title + "%" : null)
                .setFirstResult(offset)
                .setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public long count(String title) {
        String jpql = "SELECT COUNT(n) FROM Note n WHERE :title IS NULL OR n.title LIKE :title";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("title", title != null ? "%" + title + "%" : null)
                .getSingleResult();
    }

    @Override
    public Optional<Note> getByTitle(String title) {
        String jpql = "SELECT n FROM Note n WHERE n.title = :title";
        return entityManager.createQuery(jpql, Note.class)
                .setParameter("title", title)
                .getResultStream()
                .findFirst();
    }
}