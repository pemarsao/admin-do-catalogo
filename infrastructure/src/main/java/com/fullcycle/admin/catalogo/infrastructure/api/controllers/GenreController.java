package com.fullcycle.admin.catalogo.infrastructure.api.controllers;

import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreCommand;
import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreCommand;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.api.GenreAPI;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.presenters.GenreApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;
    private final ListGenreUseCase listGenreUseCase;

    public GenreController(
        final CreateGenreUseCase createGenreUseCase,
        final GetGenreByIdUseCase getGenreByIdUseCase,
        final UpdateGenreUseCase updateGenreUseCase,
        final DeleteGenreUseCase deleteGenreUseCase,
        final ListGenreUseCase listGenreUseCase
    ) {
        this.createGenreUseCase = createGenreUseCase;
        this.getGenreByIdUseCase = getGenreByIdUseCase;
        this.updateGenreUseCase = updateGenreUseCase;
        this.deleteGenreUseCase = deleteGenreUseCase;
        this.listGenreUseCase = listGenreUseCase;
    }

    @Override
    public ResponseEntity<?> create(final CreateGenreRequest input) {
        final var aCommand = CreateGenreCommand.with(input.name(), input.isActive(), input.categories());
        final var anOutput = this.createGenreUseCase.execute(aCommand);
        return ResponseEntity.created(URI.create("/genres/" + anOutput.id())).body(anOutput);
    }

    @Override
    public Pagination<GenreListResponse> list(
        final String search,
        final int page,
        final int perPage,
        final String sort,
        final String direction) {
        return this.listGenreUseCase.execute(new SearchQuery(page, perPage, search, sort, direction)).map(
            GenreApiPresenter::present
        );
    }

    @Override
    public GenreResponse getById(final String id) {
        return GenreApiPresenter.present(this.getGenreByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> update(final String id, final UpdateGenreRequest body) {
        final var aCommand = UpdateGenreCommand.with(id, body.name(), body.isActive(), body.categories());
        final var anOutput = this.updateGenreUseCase.execute(aCommand);
        return ResponseEntity.ok(anOutput);
    }

    @Override
    public void delete(final String id) {
        this.deleteGenreUseCase.execute(id);
    }
}
