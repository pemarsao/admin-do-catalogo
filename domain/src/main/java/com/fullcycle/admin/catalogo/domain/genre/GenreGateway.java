package com.fullcycle.admin.catalogo.domain.genre;

import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;

import java.util.List;
import java.util.Optional;

public interface GenreGateway {

    Genre create(Genre aGenre);

    Genre update(Genre aGenre);

    void deleteById(GenreID anID);

    Optional<Genre> findById(GenreID anID);

    Pagination<Genre> findAll(SearchQuery aQuery);

    List<GenreID> existsByIds(Iterable<GenreID> ids);

}
