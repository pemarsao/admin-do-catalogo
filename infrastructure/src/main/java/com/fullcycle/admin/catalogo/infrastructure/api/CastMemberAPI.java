package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberListResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value = "cast_members")
@Tag(name = "Cast Members", description = "Cast members management API")
public interface CastMemberAPI {

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new cast member")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Error"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    }
    )
    ResponseEntity<?> create(@RequestBody CreateCastMemberRequest input);

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Cast Members")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cast member retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    }
    )
    Pagination<CastMemberListResponse> list(
        @RequestParam(name = "search", required = false, defaultValue = "") String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "perPage", required = false, defaultValue = "10") int perPage,
        @RequestParam(name = "sort", required = false, defaultValue = "name") String sort,
        @RequestParam(name = "dir", required = false, defaultValue = "asc") String direction
    );

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Cast Member by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Return the cast member successfully"),
        @ApiResponse(responseCode = "404", description = "Cast Member not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    }
    )
    CastMemberResponse getById(@PathVariable String id);

    @PutMapping(
        value = "{id}",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update Cast Member by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Update the cast member successfully"),
        @ApiResponse(responseCode = "404", description = "Cast Member not found"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Error"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    }
    )
    ResponseEntity<?> updateById(@PathVariable String id, @RequestBody CreateCastMemberRequest input);

    @DeleteMapping(
        value = "{id}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Cast Member by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Delete the cast member successfully"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    }
    )
    void deleteById(@PathVariable String id);

}
