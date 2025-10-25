package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class ListCategoryUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultListCategoriesUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;


    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    public void givenAValidQuery_whenCallListCategories_thenShouldReturnCategories() {
        final var categories = List.of(
            Category.newCategory("Filmes", "os melhores filmes", true),
            Category.newCategory("Series", null, true)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerm = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerm, expectedSort, expectedDirection);

        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        final var expectedItemsCount = 2;
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);

        Mockito.when(categoryGateway.findAll(aQuery)).thenReturn(expectedPagination);

        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedResult, actualResult);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(categories.size(), actualResult.total());

    }

    @Test
    public void givenAValidQuery_whenNoHasResult_thenShouldReturnEmptyCategories() {
        final var categories = List.<Category>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerm = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";


        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerm, expectedSort, expectedDirection);

        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        final var expectedItemsCount = 0;
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);

        Mockito.when(categoryGateway.findAll(aQuery)).thenReturn(expectedPagination);

        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedResult, actualResult);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(categories.size(), actualResult.total());
    }

    @Test
    public void givenAValidQuery_whenGatewayThrowsException_thenReturnException() {

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerm = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var exceptionErrorMessage = "Gateway error";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerm, expectedSort, expectedDirection);

        Mockito.when(categoryGateway.findAll(aQuery)).thenThrow(new IllegalStateException(exceptionErrorMessage));

        final var exceptionResult = Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(aQuery));

        Assertions.assertEquals(exceptionResult.getMessage(), exceptionErrorMessage);
    }


}
