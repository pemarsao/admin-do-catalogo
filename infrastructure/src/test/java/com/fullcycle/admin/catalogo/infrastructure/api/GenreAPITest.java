package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreOutput;
import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.get.GenreOutput;
import com.fullcycle.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreOutput;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = GenreAPI.class)
public class GenreAPITest {

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @MockBean
    private ListGenreUseCase listGenreUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreID() throws Exception {
        //given
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");

        final var aCommand = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        Mockito.when(createGenreUseCase.execute(Mockito.any()))
                .thenReturn(CreateGenreOutput.from(expectedId));

        //when
        final var aRequest = MockMvcRequestBuilders.post("/genres")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(aCommand));

        final var response = mock.perform(aRequest)
                .andDo(print());

        //then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Location", "/genres/" + expectedId))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        Mockito.verify(createGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name()) &&
                Objects.equals(expectedIsActive, cmd.isActive()) &&
                Objects.equals(expectedCategories, cmd.categories())
            ));

    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreateGenre_shouldReturnNotification() throws Exception {
        //given
        final var expectedId = "123";
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        Mockito.when(createGenreUseCase.execute(Mockito.any()))
            .thenThrow(new NotificationException("Error message", Notification.create(new Error(expectedErrorMessage))));

        //when
        final var aRequest = MockMvcRequestBuilders.post("/genres")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(aCommand));

        final var response = mock.perform(aRequest)
            .andDo(print());

        //then
        response.andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header().string("Location", nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        Mockito.verify(createGenreUseCase).execute(argThat(cmd ->
            Objects.equals(expectedName, cmd.name()) &&
            Objects.equals(expectedIsActive, cmd.isActive()) &&
            Objects.equals(expectedCategories, cmd.categories())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetGenreById_shouldReturnGenre() throws Exception {
        //given
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.of("123", "456");

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive)
            .addCategories(expectedCategories.stream().map(
            CategoryID::from).toList());

        final var expectedId = aGenre.getId().getValue();

        Mockito.when(getGenreByIdUseCase.execute(Mockito.any()))
            .thenReturn(GenreOutput.from(aGenre));

        //when
        final var aRequest = MockMvcRequestBuilders.get("/genres/{genreId}", expectedId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var response = mock.perform(aRequest);

        //then
        response.andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId)))
            .andExpect(jsonPath("$.name", equalTo(expectedName)))
            .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
            .andExpect(jsonPath("$.categories_id", equalTo(expectedCategories)))
            .andExpect(jsonPath("$.created_at", equalTo(aGenre.getCreatedAt().toString())))
            .andExpect(jsonPath("$.updated_at", equalTo(aGenre.getUpdatedAt().toString())))
            .andExpect(jsonPath("$.deleted_at", equalTo(aGenre.getDeletedAt().toString())));

        Mockito.verify(getGenreByIdUseCase).execute(eq(expectedId));

    }

    @Test
    public void givenAnInvalidId_whenCallsGetGenreById_shouldReturnNotFound() throws Exception {
        //given
        final var expectedId = GenreID.from("123");
        final var expectedMessage = "Genre with ID 123 was not found";

        Mockito.when(getGenreByIdUseCase.execute(Mockito.any()))
            .thenThrow(NotFoundException.with(Genre.class, expectedId));

        //when
        final var aRequest = MockMvcRequestBuilders.get("/genres/{genreId}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var response = mock.perform(aRequest);

        //then
        response.andExpect(status().isNotFound())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedMessage)));

        Mockito.verify(getGenreByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreID() throws Exception {
        //given
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive)
            .addCategories(expectedCategories.stream().map(
            CategoryID::from).toList());

        final var expectedId = aGenre.getId().getValue();

        final var aCommand = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        Mockito.when(updateGenreUseCase.execute(Mockito.any()))
            .thenReturn(UpdateGenreOutput.from(aGenre));

        //when
        final var aRequest = MockMvcRequestBuilders.put("/genres/{$id}", expectedId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(aCommand));

        final var response = mock.perform(aRequest);

        //then
        response.andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId)));

        Mockito.verify(updateGenreUseCase).execute(argThat(cmd ->
            Objects.equals(expectedName, cmd.name()) &&
            Objects.equals(expectedIsActive, cmd.isActive()) &&
            Objects.equals(expectedCategories, cmd.categories())
        ));

    }

    @Test
    public void givenAnInvalidCommand_whenCallsUpdateGenre_shouldReturnNotification() throws Exception {
        //given
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");
        final var expectedErrorMessage = "'name' should not be null";

        final var aGenre = Genre.newGenre("Ação", expectedIsActive)
            .addCategories(expectedCategories.stream().map(
                CategoryID::from).toList());

        final var expectedId = aGenre.getId().getValue();

        final var aCommand = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        Mockito.when(updateGenreUseCase.execute(Mockito.any()))
            .thenThrow(new NotificationException("Error message", Notification.create(new Error(expectedErrorMessage))));

        //when
        final var aRequest = MockMvcRequestBuilders.put("/genres/{$id}", expectedId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(aCommand));

        final var response = mock.perform(aRequest);

        //then
        response.andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header().string("Location", nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        Mockito.verify(updateGenreUseCase).execute(argThat(cmd ->
            Objects.equals(expectedId, cmd.id()) &&
            Objects.equals(expectedName, cmd.name()) &&
            Objects.equals(expectedIsActive, cmd.isActive()) &&
            Objects.equals(expectedCategories, cmd.categories())
        ));
    }

    @Test
    public void givenAValidCommand_whenCallsDeleteGenre_shouldReturnNoContent() throws Exception {
        //given
        final var expectedId = "123";

        Mockito.doNothing().when(deleteGenreUseCase).execute(Mockito.any());

        //when
        final var aRequest = MockMvcRequestBuilders.delete("/genres/{genreId}", expectedId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var response = mock.perform(aRequest);

        //then
        response.andExpect(status().isNoContent());
        Mockito.verify(deleteGenreUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenAValidParams_whenCallsListGenres_shouldReturnGenres() throws Exception {
        //given
        final var aGenre = Genre.newGenre("Ação", false);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "ac";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(
            GenreListOutput.from(aGenre)
        );

        Mockito.when(listGenreUseCase.execute(Mockito.any()))
            .thenReturn(new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                expectedItems
            ));

        //when
        final var aRequest = MockMvcRequestBuilders.get("/genres")
            .queryParam("page", String.valueOf(expectedPage))
            .queryParam("perPage", String.valueOf(expectedPerPage))
            .queryParam("search", expectedTerms)
            .queryParam("sort", expectedSort)
            .queryParam("dir", expectedDirection)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var response = mock.perform(aRequest);

        //then
        response.andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
            .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
            .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
            .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
            .andExpect(jsonPath("$.items[0].id", equalTo(aGenre.getId().getValue())))
            .andExpect(jsonPath("$.items[0].name", equalTo(aGenre.getName())))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(aGenre.isActive())))
            .andExpect(jsonPath("$.items[0].created_at", equalTo(aGenre.getCreatedAt().toString())))
            .andExpect(jsonPath("$.items[0].deleted_at", equalTo(aGenre.getDeletedAt().toString())));

        Mockito.verify(listGenreUseCase).execute(argThat(searchQuery ->
            Objects.equals(expectedPage, searchQuery.page()) &&
            Objects.equals(expectedPerPage, searchQuery.perPage()) &&
            Objects.equals(expectedTerms, searchQuery.terms()) &&
            Objects.equals(expectedSort, searchQuery.sort()) &&
            Objects.equals(expectedDirection, searchQuery.direction())));
    }
}
