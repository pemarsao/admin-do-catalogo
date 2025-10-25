package com.fullcycle.admin.catalogo.infrastructure.api.controllers;

import com.fullcycle.admin.catalogo.application.castmember.create.CreateCastMemberCommand;
import com.fullcycle.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.ListCastMembersUseCase;
import com.fullcycle.admin.catalogo.application.castmember.update.UpdateCastMemberCommand;
import com.fullcycle.admin.catalogo.application.castmember.update.UpdateCastMemberUseCase;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.api.CastMemberAPI;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberListResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.presenters.CastMemberApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;
    private final UpdateCastMemberUseCase updateCastMemberUseCase;
    private final DeleteCastMemberUseCase deleteCastMemberUseCase;
    private final ListCastMembersUseCase listCastMembersUseCase;

    public CastMemberController(
        final CreateCastMemberUseCase createCastMemberUseCase,
        final GetCastMemberByIdUseCase getCastMemberByIdUseCase,
        final UpdateCastMemberUseCase updateCastMemberUseCase,
        final DeleteCastMemberUseCase deleteCastMemberUseCase,
        final ListCastMembersUseCase listCastMembersUseCase
    ) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase);
        this.updateCastMemberUseCase = Objects.requireNonNull(updateCastMemberUseCase);
        this.deleteCastMemberUseCase = Objects.requireNonNull(deleteCastMemberUseCase);
        this.listCastMembersUseCase = Objects.requireNonNull(listCastMembersUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCastMemberRequest input) {
        final var request = CreateCastMemberCommand.with(input.name(), input.type());
        final var output = this.createCastMemberUseCase.execute(request);
        return ResponseEntity.created(URI.create("/cast_members/" + output.id())).body(output);
    }

    @Override
    public Pagination<CastMemberListResponse> list(
        final String search,
        final int page,
        final int perPage,
        final String sort,
        final String direction
    ) {
        return this.listCastMembersUseCase.execute(new SearchQuery(page, perPage, search, sort, direction)).map(
            CastMemberApiPresenter::present
        );
    }

    @Override
    public CastMemberResponse getById(String id) {
        return CastMemberApiPresenter.present(this.getCastMemberByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(
        final String id,
        final CreateCastMemberRequest input
    ) {
        final var command = UpdateCastMemberCommand.with(id, input.name(), input.type());
        final var output = updateCastMemberUseCase.execute(command);
        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(String id) {
        this.deleteCastMemberUseCase.execute(id);
    }
}
