package com.fullcycle.admin.catalogo.e2e.genre;

import com.fullcycle.admin.catalogo.E2ETest;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.e2e.MockDsl;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class GenreE2ETest implements MockDsl {

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:8.2.0")
        .withPassword("123456")
        .withUsername("root")
        .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(3306));
    }

    @Autowired
    private MockMvc mvc;

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateAGenreWithValidValues() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var actualId = givenGenre(expectedName, expectedIsActive, expectedCategories);

        final var actualGenre = genreRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(actualGenre.getCategoriesIds().size() == expectedCategories.size()
            && actualGenre.getCategoriesIds().containsAll(expectedCategories));
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateAGenreWithCategories() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        final var categories = givenCategory("Filmes", "A categoria mais assistida", true);
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(categories);

        final var actualId = givenGenre(expectedName, expectedIsActive, expectedCategories);

        final var actualGenre = genreRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(actualGenre.getCategoriesIds().size() == expectedCategories.size()
                              && actualGenre.getCategoriesIds().containsAll(expectedCategories));
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateToAllGenre() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        givenGenre("Ação", true, List.of());
        givenGenre("Esporte", true, List.of());
        givenGenre("Drama", true, List.of());

        listGenre(0, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Ação")));

        listGenre(1, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Drama")));

        listGenre(2, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(2)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Esporte")));

        listGenre(3, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(3)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(0)));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchBetweenAllGenre() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        givenGenre("Ação", true, List.of());
        givenGenre("Esporte", true, List.of());
        givenGenre("Drama", true, List.of());

        listGenre(0, 1, "Dra")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Drama")));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllGenreByNameDesc() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        givenGenre("Ação", true, List.of());
        givenGenre("Esporte", true, List.of());
        givenGenre("Drama", true, List.of());

        listGenre(0, 3, "", "name", "desc")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(3)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(3)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Esporte")))
            .andExpect(jsonPath("$.items[1].name", equalTo("Drama")))
            .andExpect(jsonPath("$.items[2].name", equalTo("Ação")));
    }

    @Test
    public void asACategoryAdminIShouldBeAbleToGetAGenreByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        final var categories = givenCategory("Filmes", "A categoria mais assistida", true);
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(categories);

        final var actualId = givenGenre(expectedName, expectedIsActive, expectedCategories);

        final var actualGenre = retrieveGenre(actualId);

        Assertions.assertNotNull(actualGenre.id());
        Assertions.assertEquals(expectedName, actualGenre.name());
        Assertions.assertTrue(
            actualGenre.categories().size() == expectedCategories.size()
            && mapTo(expectedCategories, CategoryID::getValue).containsAll(actualGenre.categories()));
        Assertions.assertEquals(expectedIsActive, actualGenre.active());
        Assertions.assertNotNull(actualGenre.createdAt());
        Assertions.assertNotNull(actualGenre.updatedAt());
        Assertions.assertNull(actualGenre.deletedAt());

    }

    @Test
    public void asACategoryAdminIShouldBeToSeeATreatedErrorByGettingANotFoundGenre() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var actualId = givenGenre(expectedName, expectedIsActive, expectedCategories);

        getGenre(CategoryID.from("123"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", equalTo("Genre with ID 123 was not found")));

    }

    @Test
    public void asACategoryAdminIShouldBeAbleToUpdateAGenreByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        final var categories = givenCategory("Filmes", "A categoria mais assistida", true);
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(categories);

        final var actualId = givenGenre("acao", expectedIsActive, expectedCategories);

        final var aRequestBody = new UpdateGenreRequest(expectedName, mapTo(expectedCategories, CategoryID::getValue), expectedIsActive);
        updateGenre(actualId, aRequestBody)
            .andExpect(status().isOk());

        final var actualGenre = genreRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertTrue(
            actualGenre.getCategoriesIds().size() == expectedCategories.size()
            && expectedCategories.containsAll(actualGenre.getCategoriesIds()));
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void asACategoryAdminIShouldBeAbleToInactiveAGenreByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        final var categories = givenCategory("Filmes", "A categoria mais assistida", true);
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.of(categories);

        final var actualId = givenGenre(expectedName, true, expectedCategories);

        final var aRequestBody = new UpdateGenreRequest(expectedName, mapTo(expectedCategories, CategoryID::getValue), expectedIsActive);
        updateGenre(actualId, aRequestBody)
            .andExpect(status().isOk());

        final var actualGenre = genreRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertTrue(
            actualGenre.getCategoriesIds().size() == expectedCategories.size()
            && expectedCategories.containsAll(actualGenre.getCategoriesIds()));
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNotNull(actualGenre.getDeletedAt());

    }

    @Test
    public void asACategoryAdminIShouldBeAbleToActiveAGenreByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var actualId = givenGenre(expectedName, false, expectedCategories);

        final var aRequestBody = new UpdateGenreRequest(expectedName, mapTo(expectedCategories, CategoryID::getValue), expectedIsActive);
        updateGenre(actualId, aRequestBody)
            .andExpect(status().isOk());

        final var actualCategory = genreRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedCategories, actualCategory.getCategoriesIds());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

    }

    @Test
    public void asACategoryAdminIShouldBeAbleToDeleteAGenreByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var actualId = givenGenre(expectedName, expectedIsActive, expectedCategories);

        deleteGenre(actualId)
            .andExpect(status().isNoContent());

        Assertions.assertFalse(this.genreRepository.existsById(actualId.getValue()));
        Assertions.assertEquals(genreRepository.count(), 0);

    }

    @Test
    public void asACategoryAdminIShouldNotSeeAErrorByDeletingANotExistentGenre() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(genreRepository.count(), 0);

        deleteGenre(GenreID.from("1345"))
            .andExpect(status().isNoContent());

        Assertions.assertFalse(this.genreRepository.existsById("1345"));
        Assertions.assertEquals(genreRepository.count(), 0);

    }

}
