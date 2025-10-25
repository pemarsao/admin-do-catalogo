package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository repository;

    public CategoryMySQLGateway(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category create(final Category category) {
        return save(category);
    }

    @Override
    public void deleteById(final CategoryID id) {
        final String idValue = id.getValue();
        if (repository.existsById(idValue)) {
            repository.deleteById(idValue);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryID id) {
        return this.repository.findById(id.getValue())
            .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category category) {
        return save(category);
    }

    @Override
    public Pagination<Category> findAll(final SearchQuery query) {
        //Paginação
        var page = PageRequest.of(query.page(),
            query.perPage(),
            Sort.by(Sort.Direction.fromString(query.direction()), query.sort()));
        // Busca dinamica pelo nome e descrição
        final var specifications =
            Optional.ofNullable(query.terms()).filter(str -> !str.isBlank())
                .map(str -> {
                    return SpecificationUtils
                        .<CategoryJpaEntity>like("name", str)
                        .or(SpecificationUtils.like("description", str));
                }).orElse(null);

        final var pageResult = this.repository.findAll(Specification.where(specifications), page);

        return new Pagination<>(
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public List<CategoryID> existsByIds(Iterable<CategoryID> categoryIDs) {
        final var ids = StreamSupport.stream(categoryIDs.spliterator(), false)
            .map(CategoryID::getValue)
            .toList();
        return this.repository.existsByIds(ids)
            .stream()
            .map(CategoryID::from)
            .toList();
    }

    private Category save(final Category category) {
        return this.repository.save(CategoryJpaEntity.from(category)).toAggregate();
    }
}
