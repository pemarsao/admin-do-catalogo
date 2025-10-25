package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalogo.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalogo.application.category.retrieve.get.CategoryOutput;
import com.fullcycle.admin.catalogo.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalogo.application.category.retrieve.list.CategoryListOutput;
import com.fullcycle.admin.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.fullcycle.admin.catalogo.application.category.update.UpdateCategoryOutput;
import com.fullcycle.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import io.vavr.API;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import java.util.List;
import java.util.Objects;

import static io.vavr.API.Right;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {

    @Autowired
    MockMvc mock;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    ListCategoriesUseCase listCategoriesUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var anInput = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
            .thenReturn(Right(CreateCategoryOutput.from("123")));

        MockHttpServletRequestBuilder request = post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(anInput));

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/categories/123"))
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo("123")));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
            Objects.equals(expectedName, cmd.name()) &&
            Objects.equals(expectedDescription, cmd.description()) &&
            Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAInvalidName_whenCallsCreateCategory_thenShouldReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
            .thenReturn(API.Left(Notification.create(new Error(expectedErrorMessage))));

        MockHttpServletRequestBuilder request = post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(anInput));

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Location", Matchers.nullValue()))
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
            Objects.equals(expectedName, cmd.name()) &&
            Objects.equals(expectedDescription, cmd.description()) &&
            Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAInvalidCommand_whenCallsCreateCategory_thenShouldReturnDomainException() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
            .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        MockHttpServletRequestBuilder request = post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(anInput));

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Location", Matchers.nullValue()))
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
            Objects.equals(expectedName, cmd.name()) &&
            Objects.equals(expectedDescription, cmd.description()) &&
            Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidCommand_whenCallsGetCategory_shouldReturnCategory() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId().getValue();

        when(getCategoryByIdUseCase.execute(any())).thenReturn(CategoryOutput.from(aCategory));

        MockHttpServletRequestBuilder request = get("/categories/{id}", expectedId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(expectedId)))
            .andExpect(jsonPath("$.name", equalTo(expectedName)))
            .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
            .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
            .andExpect(jsonPath("$.created_at", equalTo(aCategory.getCreatedAt().toString())))
            .andExpect(jsonPath("$.updated_at", equalTo(aCategory.getUpdatedAt().toString())))
            .andExpect(jsonPath("$.deleted_at", equalTo(aCategory.getDeletedAt())));

        verify(getCategoryByIdUseCase, times(1)).execute(eq(expectedId));

    }

    @Test
    public void givenAInvalidCommand_whenCallsGetCategory_shouldReturnNotFound() throws Exception {
        final var expectedId = CategoryID.from("123");
        final var expectedMessage = "Category with ID 123 was not found";

        when(getCategoryByIdUseCase.execute(any()))
            .thenThrow(NotFoundException.with(
                Category.class,
                expectedId
            ));

        MockHttpServletRequestBuilder request = get("/categories/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", equalTo(expectedMessage)));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() throws Exception {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var anInput = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(updateCategoryUseCase.execute(any()))
            .thenReturn(Right(UpdateCategoryOutput.from("123")));

        MockHttpServletRequestBuilder request = put("/categories/{categoryId}", expectedId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(anInput));

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd ->
            Objects.equals(expectedName, cmd.name()) &&
            Objects.equals(expectedDescription, cmd.description()) &&
            Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAInvalidCommand_whenCallsUpdateCategory_shouldReturnNotFound() throws Exception {
        final var expectedId = CategoryID.from("not-found");
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var anInput = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var expectedMessage = "Category with ID not-found was not found";

        when(updateCategoryUseCase.execute(any()))
            .thenThrow(NotFoundException.with(
                Category.class,
                expectedId
            ));

        MockHttpServletRequestBuilder request = put("/categories/{categoryId}", expectedId.getValue())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(anInput));

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", equalTo(expectedMessage)));
    }

    @Test
    public void givenAInvalidCommand_whenCallsUpdateCategory_thenShouldReturnDomainException() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var anInput = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(updateCategoryUseCase.execute(any()))
            .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        MockHttpServletRequestBuilder request = put("/categories/{categoryId}", "123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(anInput));

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Location", Matchers.nullValue()))
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd ->
            Objects.equals(expectedName, cmd.name()) &&
            Objects.equals(expectedDescription, cmd.description()) &&
            Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }


    @Test
    public void givenAValidID_whenCallsDeleteCategory_shouldBeNoContent() throws Exception {
        final var expectedId = "123";

        doNothing().when(deleteCategoryUseCase).execute(expectedId);

        MockHttpServletRequestBuilder request = delete("/categories/{categoryId}", "123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1)).execute(any());
    }

    @Test
    public void givenAValidParams_whenCallsListCategories_thenReturnsCategories() throws Exception {
        final var aCategory = Category.newCategory(
            "Filmes",
            "A categoria mais assistida",
            true
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "filmes";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        when(listCategoriesUseCase.execute(any()))
            .thenReturn(new Pagination<CategoryListOutput>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                List.of(CategoryListOutput.from(aCategory))
            ));

        MockHttpServletRequestBuilder request = get("/categories")
            .param("page", String.valueOf(expectedPage))
            .param("perPage", String.valueOf(expectedPerPage))
            .param("sort", expectedSort)
            .param("dir", expectedDirection)
            .param("search", expectedTerms)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mock.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
            .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
            .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
            .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
            .andExpect(jsonPath("$.items[0].id", equalTo(aCategory.getId().getValue())))
            .andExpect(jsonPath("$.items[0].name", equalTo(aCategory.getName())))
            .andExpect(jsonPath("$.items[0].description", equalTo(aCategory.getDescription())))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(aCategory.isActive())))
            .andExpect(jsonPath("$.items[0].created_at", equalTo(aCategory.getCreatedAt().toString())))
            .andExpect(jsonPath("$.items[0].deleted_at", equalTo(aCategory.getDeletedAt())));

        verify(listCategoriesUseCase, times(1)).execute(argThat(query ->
            Objects.equals(expectedPage, query.page()) &&
            Objects.equals(expectedPerPage, query.perPage()) &&
            Objects.equals(expectedTerms, query.terms()) &&
            Objects.equals(expectedSort, query.sort()) &&
            Objects.equals(expectedDirection, query.direction())
        ));
    }
}
