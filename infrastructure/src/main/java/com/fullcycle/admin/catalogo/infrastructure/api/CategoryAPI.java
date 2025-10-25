package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
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

@RequestMapping(value = "categories")
@Tag(name = "Categories", description = "Category management API")
public interface CategoryAPI {

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Error"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    }

    )
    ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest input);

    @GetMapping(
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "List categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listed successfully"),
        @ApiResponse(responseCode = "422", description = "A invalid parameter was provided"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    Pagination<?> listCategories(
        @RequestParam(name = "search", required = false, defaultValue = "") String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "perPage", required = false, defaultValue = "10") int perPage,
        @RequestParam(name = "sort", required = false, defaultValue = "name") String sort,
        @RequestParam(name = "dir", required = false, defaultValue = "asc") String direction
    );

    @GetMapping(value = "{id}",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a category by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    CategoryResponse getById(@PathVariable(name = "id") String id);

    @PutMapping(value = "{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a category by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> updateCategory(@PathVariable(name = "id") String id, @RequestBody UpdateCategoryRequest input);

    @DeleteMapping(
        value = "{id}"
    )
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    void deleteById(@PathVariable(name = "id") String id);
}
