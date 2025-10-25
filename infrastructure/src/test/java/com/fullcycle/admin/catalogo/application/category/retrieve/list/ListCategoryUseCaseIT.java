package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

@IntegrationTest
public class ListCategoryUseCaseIT {

    @Autowired
    private ListCategoriesUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @BeforeEach
    void mockUp() {
        final var categories = Stream.of(
            Category.newCategory("Filmes", "os melhores filmes", true),
            Category.newCategory("Netflix Originals", "Titulos Originais da Netflix", true),
            Category.newCategory("Amazon Originals", "Titulos Originais da Amazon", true),
            Category.newCategory("Documentários", null, true),
            Category.newCategory("Sports", null, true),
            Category.newCategory("Kids", "Titulos para crianças", true),
            Category.newCategory("Series", null, true)
        ).map(CategoryJpaEntity::from).toList();

        repository.saveAllAndFlush(categories);
    }

    @Test
    public void givenAValidTerm_whenTermDoesntMatchsPrePersistedCategories_thenReturnsEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerm = "dsdsdas";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerm, expectedSort, expectedDirection);
        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
    }

    @ParameterizedTest
    @CsvSource({
        "fil,0,10,1,1,Filmes",
        "net,0,10,1,1,Netflix Originals",
        "ZON,0,10,1,1,Amazon Originals",
        "KID,0,10,1,1,Kids",
        "crianças,0,10,1,1,Kids",
        "da Amazon,0,10,1,1,Amazon Originals"
    })
    public void givenAValidTerm_whenCallsListCategories_thenReturnsCategoriesFiltered(
        final String expectedTerm,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerm, expectedSort, expectedDirection);
        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
        "name,asc,0,10,7,7,Amazon Originals",
        "name,desc,0,10,7,7,Sports",
        "createdAt,asc,0,10,7,7,Filmes",
        "createdAt,desc,0,10,7,7,Series"
    })
    public void givenAValidSortAndDirection_whenCallsListCategories_thenReturnsCategoriesSorted(
        final String expectedSort,
        final String expectedDirection,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoryName
    ) {
        final var expectedTerm = "";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerm, expectedSort, expectedDirection);
        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
        "0,2,2,7,Amazon Originals;Documentários",
        "1,2,2,7,Filmes;Kids",
        "2,2,2,7,Netflix Originals;Series",
        "3,2,1,7,Sports"
    })
    public void givenAValidPage_whenCallListCategories_thenReturnsCategoriesPaginated(
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTerm = "";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerm, expectedSort, expectedDirection);
        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());

        int index = 0;
        for (String expectedName : expectedCategoryName.split(";")) {
            final var actualName = actualResult.items().get(index).name();
            Assertions.assertEquals(expectedName, actualName);
            index++;
        }
    }

}
