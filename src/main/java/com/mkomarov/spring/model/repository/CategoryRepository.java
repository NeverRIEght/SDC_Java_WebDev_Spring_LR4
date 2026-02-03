package com.mkomarov.spring.model.repository;

import com.mkomarov.spring.model.entity.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Optional<Category> getById(UUID id);

    List<Category> getAll(String name, int limit, int offset, String sortBy, String direction);
    Category create(Category category);
    boolean update(Category category);
    Optional<Category> delete(UUID id);
    long count(String title);
    Optional<Category> getByName(String title);
}
