package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository repository;



    @Test
    public void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, repository.count());

        final var actualCategory = categoryGateway.create(aCategory);

        Assertions.assertEquals(1, repository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = repository.findById(actualCategory.getId().getValue()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertNotNull(actualEntity.getCreatedAt());
        Assertions.assertNotNull(actualEntity.getUpdatedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());

    }

    @Test
    public void givenAValidCategory_whenCallsUpdate_shouldReturnACategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory("Film", null, expectedIsActive);

        Assertions.assertEquals(0, repository.count());

        this.repository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        final var aInvalidCategory = this.repository.findById(aCategory.getId().getValue()).get();
        Assertions.assertEquals(aInvalidCategory.getName(), "Film");
        Assertions.assertNull(aInvalidCategory.getDescription());
        Assertions.assertTrue(aInvalidCategory.isActive());

        Assertions.assertEquals(1, repository.count());

        final var updateCategory = aCategory.clone().update(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = categoryGateway.update(updateCategory);

        Assertions.assertEquals(1, repository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(actualCategory.getCreatedAt(), aCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = repository.findById(actualCategory.getId().getValue()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertNotNull(actualEntity.getCreatedAt());
        Assertions.assertNotNull(actualEntity.getUpdatedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());

    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenCallsTryToDeleteIt_shouldDeleteTheCategory() {
        final var aCategory = Category.newCategory("Filmes", "A categoria mais assistida", true);

        repository.saveAndFlush(CategoryJpaEntity.from(aCategory));
        Assertions.assertEquals(1, repository.count());

        categoryGateway.deleteById(aCategory.getId());
        Assertions.assertEquals(0, repository.count());

    }

    @Test
    public void givenAInvalidCategoryId_whenCallsTryToDeleteIt_shouldDeleteTheCategory() {
        Assertions.assertEquals(0, repository.count());

        categoryGateway.deleteById(CategoryID.from("invalid"));

        Assertions.assertEquals(0, repository.count());
    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenCallsTryToFindByIdIt_shouldReturnACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, repository.count());

        this.repository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, repository.count());

        final var actualCategory = categoryGateway.findById(aCategory.getId()).get();

        Assertions.assertEquals(1, repository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(actualCategory.getCreatedAt(), aCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

    }

    @Test
    public void givenAValidCategoryIdNotStored_whenCallsFindById_shouldReturnEmpty() {

        Assertions.assertEquals(0, repository.count());

        final var actualCategory = categoryGateway.findById(CategoryID.from("empty"));

        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAll_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Series", "A categoria assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        Assertions.assertEquals(0, repository.count());

        repository.saveAll(List.of(CategoryJpaEntity.from(filmes), CategoryJpaEntity.from(series), CategoryJpaEntity.from(documentarios)));

        Assertions.assertEquals(3, repository.count());

        final var query = new SearchQuery(0, 1, "", "name", "asc");
        final var actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(documentarios.getId(), actualResult.items().get(0).getId());

    }

    @Test
    public void givenEmptyCategoriesTable_whenCallsFindAll_shouldBeEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        Assertions.assertEquals(0, repository.count());


        final var query = new SearchQuery(0, 1, "", "name", "asc");
        final var actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(0, actualResult.items().size());
    }

    @Test
    public void givenFollowPagination_whenCallsFindAllWithPage1_shouldReturnPaginated() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Series", "A categoria assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        Assertions.assertEquals(0, repository.count());

        repository.saveAll(List.of(CategoryJpaEntity.from(filmes), CategoryJpaEntity.from(series), CategoryJpaEntity.from(documentarios)));

        Assertions.assertEquals(3, repository.count());

        var query = new SearchQuery(0, 1, "", "name", "asc");
        var actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(documentarios.getId(), actualResult.items().get(0).getId());

        //PAGE 1
        expectedPage = 1;
        query = new SearchQuery(1, 1, "", "name", "asc");
        actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(filmes.getId(), actualResult.items().get(0).getId());

        //PAGE 2
        expectedPage = 2;
        query = new SearchQuery(2, 1, "", "name", "asc");
        actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(series.getId(), actualResult.items().get(0).getId());

    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAllWhenDocAsTermMatchCategoryName_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Series", "A categoria assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        Assertions.assertEquals(0, repository.count());

        repository.saveAll(List.of(CategoryJpaEntity.from(filmes), CategoryJpaEntity.from(series), CategoryJpaEntity.from(documentarios)));

        Assertions.assertEquals(3, repository.count());

        final var query = new SearchQuery(0, 1, "doc", "name", "asc");
        final var actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(documentarios.getId(), actualResult.items().get(0).getId());

    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAllWhenMaisAssistidaAsTermMatchCategoryDescription_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Series", "Uma categoria assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        Assertions.assertEquals(0, repository.count());

        repository.saveAll(List.of(CategoryJpaEntity.from(filmes), CategoryJpaEntity.from(series), CategoryJpaEntity.from(documentarios)));

        Assertions.assertEquals(3, repository.count());

        final var query = new SearchQuery(0, 1, "MAIS ASSISTIDA", "name", "asc");
        final var actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(filmes.getId(), actualResult.items().get(0).getId());

    }

    @Test
    public void givenPrePersistedCategories_whenCallsExistsByIds_shouldReturnIds() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Series", "Uma categoria assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        Assertions.assertEquals(0, repository.count());

        repository.saveAll(List.of(CategoryJpaEntity.from(filmes), CategoryJpaEntity.from(series), CategoryJpaEntity.from(documentarios)));

        Assertions.assertEquals(3, repository.count());

        final var expectedIds = List.of(filmes.getId(), series.getId());
        final var ids = List.of(filmes.getId(), series.getId(), CategoryID.from("123"));
        final var actualResult = categoryGateway.existsByIds(ids);

        Assertions.assertTrue(expectedIds.size() == actualResult.size()
            && expectedIds.containsAll(actualResult));


    }
}
