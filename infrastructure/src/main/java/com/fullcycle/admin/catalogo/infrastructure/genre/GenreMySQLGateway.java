package com.fullcycle.admin.catalogo.infrastructure.genre;

import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreMySQLGateway(final GenreRepository genreRepository) {
        this.genreRepository = Objects.requireNonNull(genreRepository);
    }

    @Override
    public Genre create(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    public Genre update(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    public void deleteById(final GenreID anID) {
        final var genreId = anID.getValue();
        if (this.genreRepository.existsById(genreId)) {
            this.genreRepository.deleteById(genreId);
        }
    }

    @Override
    public Optional<Genre> findById(final GenreID anID) {
        return this.genreRepository.findById(anID.getValue()).map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Pagination<Genre> findAll(SearchQuery aQuery) {

        final var page = PageRequest.of(
            aQuery.page(),
            aQuery.perPage(),
            Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var where = Optional.ofNullable(aQuery.terms())
            .filter(str -> !str.isBlank())
            .map(this::assembleSpecification)
            .orElse(null);

        final var pageResults = this.genreRepository.findAll(Specification.where(where), page);
        return new Pagination<>(
            pageResults.getNumber(),
            pageResults.getSize(),
            pageResults.getTotalElements(),
            pageResults
                .map(GenreJpaEntity::toAggregate)
                .toList()
        );
    }

    @Override
    public List<GenreID> existsByIds(Iterable<GenreID> genreIDS) {
        final var ids = StreamSupport.stream(genreIDS.spliterator(), false)
                .map(GenreID::getValue)
                .toList();
        return this.genreRepository.existsByIds(ids)
                .stream()
                .map(GenreID::from)
                .toList();
    }

    private Specification<GenreJpaEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }

    private Genre save(Genre aGenre) {
        return this.genreRepository.save(GenreJpaEntity.from(aGenre)).toAggregate();
    }
}
