package com.mkomarov.spring.model.repository.springdata;

import com.mkomarov.spring.model.entity.Category;
import com.mkomarov.spring.model.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("spring-data")
@RequiredArgsConstructor
public class SpringDataCategoryRepositoryAdapter implements CategoryRepository {
    private final SpringDataCategoryRepository repository;

    @Override
    public Optional<Category> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Set<Category> getAll(String name, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return repository.findAllByName(name, pageable)
                .stream()
                .collect(Collectors.toSet());
    }

    @Override
    public Category create(Category category) {
        return repository.save(category);
    }

    @Override
    public boolean update(Category category) {
        if (repository.existsById(category.getId())) {
            repository.save(category);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Category> delete(UUID id) {
        return repository.findById(id).map(category -> {
            repository.delete(category);
            return category;
        });
    }

    @Override
    public long count(String name) {
        return (name == null || name.isEmpty())
                ? repository.count()
                : repository.findAllByName(name, Pageable.unpaged()).getTotalElements();
    }

    @Override
    public Optional<Category> getByName(String name) {
        return repository.findByName(name);
    }
}
