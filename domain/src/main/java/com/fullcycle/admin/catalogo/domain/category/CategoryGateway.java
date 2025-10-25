package com.fullcycle.admin.catalogo.domain.category;

import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;

import java.util.List;
import java.util.Optional;

public interface CategoryGateway {

    Category create(Category category);

    void deleteById(CategoryID id);

    Optional<Category> findById(CategoryID id);

    Category update(Category category);

    Pagination<Category> findAll(SearchQuery query);

    List<CategoryID> existsByIds(Iterable<CategoryID> ids);
}
