package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Category;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CategoryRepository {
    Optional<Category> getById(UUID id);
    Set<Category> getAll(String title, int limit, int offset);
    Category create(Category category);
    boolean update(Category category);
    Optional<Category> delete(UUID id);
    long count(String title);
    Optional<Category> getByName(String title);
}
