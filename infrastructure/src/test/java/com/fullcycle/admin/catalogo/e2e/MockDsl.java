package com.fullcycle.admin.catalogo.e2e;

import com.fullcycle.admin.catalogo.ApiTest;
import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.configuration.json.Json;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    /**
     * CastMember
     */

    default ResultActions deleteCastMember(final CastMemberID anId) throws Exception {
        return deleteMock("/cast_members/", anId);
    }

    default CastMemberID givenCastMember(final String expectedName, final CastMemberType type) throws
            Exception {
        final var aRequestBody = new CreateCastMemberRequest(expectedName, type);
        final var actualId = given("/cast_members", aRequestBody);
        return CastMemberID.from(actualId);
    }

    default ResultActions givenCastMemberResult(final String expectedName, final CastMemberType type) throws Exception {
        final var aRequestBody = new CreateCastMemberRequest(expectedName, type);
        return givenResult("/cast_members", aRequestBody);
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search) throws Exception {
        return listCastMembers(page, perPage, search, "", "");
    }

    default ResultActions listCastMembers(final int page, final int perPage) throws Exception {
        return listCastMembers(page, perPage, "", "", "");
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return list("/cast_members", page, perPage, search, sort, direction);
    }

    default CastMemberResponse retrieveCastMember(final Identifier anId) throws Exception {
        return retrieve("/cast_members/", anId, CastMemberResponse.class);
    }

    default ResultActions getCastMember(final Identifier anId) throws Exception {
        return getIdentifier("/cast_members/", anId);
    }

    default ResultActions updateCastMember(final Identifier anId, final String expectedName, final CastMemberType type) throws Exception {
        return update("/cast_members/", anId, new UpdateCastMemberRequest(expectedName, type));
    }

    /**
     * Category
     */

    default ResultActions deleteCategory(final CategoryID anId) throws Exception {
        return deleteMock("/categories/", anId);
    }

    default CategoryID givenCategory(final String expectedName, final String expectedDescription, final boolean expectedIsActive) throws
            Exception {
        final var aRequestBody = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var actualId = given("/categories", aRequestBody);
        return CategoryID.from(actualId);
    }

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return list("/categories", page, perPage, search, sort, direction);
    }

    default CategoryResponse retrieveCategory(final Identifier anId) throws Exception {
        return retrieve("/categories/", anId, CategoryResponse.class);
    }

    default ResultActions updateCategory(final Identifier anId, final UpdateCategoryRequest body) throws Exception {
        return update("/categories/", anId, body);
    }

    default ResultActions getCategory(final Identifier anId) throws Exception {
        return getIdentifier("/categories/", anId);
    }

    /**
     * Genres
     */

    default GenreID givenGenre(final String expectedName, final boolean expectedIsActive, List<CategoryID> expectedCategories) throws
            Exception {
        final var aRequestBody = new CreateGenreRequest(expectedName, mapTo(expectedCategories, CategoryID::getValue), expectedIsActive);
        final var actualId = given("/genres", aRequestBody);
        return GenreID.from(actualId);
    }

    default ResultActions getGenre(final Identifier anId) throws Exception {
        return getIdentifier("/genres/", anId);
    }

    default ResultActions listGenre(final int page, final int perPage, final String search) throws Exception {
        return listGenre(page, perPage, search, "", "");
    }

    default ResultActions listGenre(final int page, final int perPage) throws Exception {
        return listGenre(page, perPage, "", "", "");
    }

    default ResultActions listGenre(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return list("/genres", page, perPage, search, sort, direction);
    }

    default ResultActions deleteGenre(final GenreID anId) throws Exception {
        return deleteMock("/genres/", anId);
    }

    default GenreResponse retrieveGenre(final Identifier anId) throws Exception {
        return retrieve("/genres/", anId, GenreResponse.class);
    }

    default ResultActions updateGenre(final Identifier anId, final UpdateGenreRequest body) throws Exception {
        return update("/genres/", anId, body);
    }

    /**
     * Helpers
     */

    private ResultActions deleteMock(final String url, final Identifier anId) throws Exception {
        final var aRequest = delete(url + anId.getValue())
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest)
                .andExpect(status().isNoContent());
    }

    private String given(final String url, Object body) throws Exception {
        final var aRequest = post(url)
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc().perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("%s/".formatted(url), "");

        return actualId;
    }

    private ResultActions givenResult(final String url, Object body) throws Exception {
        final var aRequest = post(url)
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return this.mvc().perform(aRequest);
    }

    private ResultActions update(final String url, final Identifier anId, final Object body) throws Exception {
        final var aRequest = put(url + anId.getValue())
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return this.mvc().perform(aRequest);
    }

    private ResultActions list(final String url, final int page, final int perPage, final String search, final String sort, final String direction) throws
            Exception {
        final var aRequest = get(url).param("page", String.valueOf(page))
                .with(ApiTest.ADMIN_JWT)
                .param("perPage", String.valueOf(perPage))
                .param("search", search)
                .param("sort", sort)
                .param("dir", direction)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest);
    }

    private ResultActions getIdentifier(final String url, final Identifier anId) throws Exception {
        final var aRequest = get(url + anId.getValue())
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        return this.mvc().perform(aRequest);
    }

    default <A, D> List<A> mapTo(final List<D> list, final Function<D, A> mapper) {
        return list
                .stream()
                .map(mapper)
                .toList();
    }

    private <T> T retrieve(final String url, final Identifier anId, final Class<T> clazz) throws Exception {
        final var aRequest = get(url + anId.getValue())
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        final var json = this.mvc().perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return Json.readValue(json, clazz);
    }

}
