package com.mkomarov.spring.model.repository.springdata;

import com.mkomarov.spring.model.entity.Category;
import com.mkomarov.spring.model.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpringDataCategoryRepositoryAdapter implements CategoryRepository {
    private final SpringDataCategoryRepository repository;

    @Override
    public Optional<Category> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Category> getAll(String name, int limit, int offset, String sortBy, String direction) {
        int pageNumber = offset / limit;

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, limit, sort);
        return repository.findAllByName(name, pageable)
                .stream()
                .toList();
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
