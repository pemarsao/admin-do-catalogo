package com.fullcycle.admin.catalogo.e2e.category;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fullcycle.admin.catalogo.E2ETest;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.e2e.MockDsl;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CategoryE2ETest implements MockDsl {

    @Container
    @ServiceConnection
    private static final MariaDBContainer<?> MYSQL_CONTAINER =
            new MariaDBContainer<>("mariadb:11")
                    .withDatabaseName("adm_videos")
                    .withUsername("test")
                    .withPassword("test")
                    .withStartupTimeout(Duration.ofMinutes(5))
                    .withReuse(true);

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
    }

    @Autowired
    private MockMvc mvc;

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void asACategoryAdminIShouldBeAbleToCreateACategoryWithValidValues() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateToAllCategories() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        givenCategory("Filmes", null, true);
        givenCategory("Documentários", null, true);
        givenCategory("Series", null, true);

        listCategories(0, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")));

        listCategories(1, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")));

        listCategories(2, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(2)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Series")));

        listCategories(3, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(3)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(0)));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        givenCategory("Filmes", null, true);
        givenCategory("Documentários", null, true);
        givenCategory("Series", null, true);

        listCategories(0, 1, "fil")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        givenCategory("Filmes", "C", true);
        givenCategory("Documentários", "Z", true);
        givenCategory("Series", "A", true);

        listCategories(0, 3, "", "description", "desc")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(3)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(3)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")))
            .andExpect(jsonPath("$.items[1].name", equalTo("Filmes")))
            .andExpect(jsonPath("$.items[2].name", equalTo("Series")));
    }

    @Test
    public void asACategoryAdminIShouldBeAbleToGetACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = retrieveCategory(actualId);

        Assertions.assertNotNull(actualCategory.id());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.active());
        Assertions.assertNotNull(actualCategory.createdAt());
        Assertions.assertNotNull(actualCategory.updatedAt());
        Assertions.assertNull(actualCategory.deletedAt());

    }

    @Test
    public void asACategoryAdminIShouldBeToSeeATreatedErrorByGettingANotFoundCategory() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory(expectedName, expectedDescription, expectedIsActive);

        getCategory(CategoryID.from("123"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", equalTo("Category with ID 123 was not found")));

    }

    @Test
    public void asACategoryAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory("Movies", null, true);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        updateCategory(actualId, aRequestBody)
            .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

    }

    @Test
    public void asACategoryAdminIShouldBeAbleToInactiveACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var actualId = givenCategory(expectedName, expectedDescription, true);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        updateCategory(actualId, aRequestBody)
            .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());

    }

    @Test
    public void asACategoryAdminIShouldBeAbleToActiveACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory(expectedName, expectedDescription, false);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        updateCategory(actualId, aRequestBody)
            .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

    }

    @Test
    public void asACategoryAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory(expectedName, expectedDescription, expectedIsActive);

        deleteCategory(actualId)
            .andExpect(status().isNoContent());

        Assertions.assertFalse(this.categoryRepository.existsById(actualId.getValue()));

    }

    @Test
    public void asACategoryAdminIShouldNotSeeAErrorByDeletingANotExistentCategory() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(categoryRepository.count(), 0);

        deleteCategory(CategoryID.from("1345"))
            .andExpect(status().isNoContent());

        Assertions.assertFalse(this.categoryRepository.existsById("1345"));
        Assertions.assertEquals(categoryRepository.count(), 0);

    }

}
