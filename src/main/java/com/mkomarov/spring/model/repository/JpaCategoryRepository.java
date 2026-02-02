package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@Profile("jpa")
public class JpaCategoryRepository implements CategoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Category> getById(UUID id) {
        return Optional.ofNullable(entityManager.find(Category.class, id));
    }

    @Override
    @Transactional
    public Category create(Category category) {
        entityManager.persist(category);
        return category;
    }

    @Override
    @Transactional
    public boolean update(Category category) {
        if (entityManager.find(Category.class, category.getId()) == null) {
            return false;
        }
        entityManager.merge(category);
        return true;
    }

    @Override
    @Transactional
    public Optional<Category> delete(UUID id) {
        return getById(id).map(category -> {
            entityManager.remove(category);
            return category;
        });
    }

    @Override
    public Set<Category> getAll(String name, int limit, int offset) {
        String jpql = "SELECT c FROM Category c WHERE :name IS NULL OR c.name LIKE :name";

        TypedQuery<Category> query = entityManager.createQuery(jpql, Category.class)
                .setParameter("name", name != null ? "%" + name + "%" : null)
                .setFirstResult(offset)
                .setMaxResults(limit);

        return new HashSet<>(query.getResultList());
    }

    @Override
    public long count(String name) {
        String jpql = "SELECT COUNT(c) FROM Category c WHERE :name IS NULL OR c.name LIKE :name";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("name", name != null ? "%" + name + "%" : null)
                .getSingleResult();
    }

    @Override
    public Optional<Category> getByName(String name) {
        String jpql = "SELECT c FROM Category c WHERE c.name = :name";
        return entityManager.createQuery(jpql, Category.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }
}
