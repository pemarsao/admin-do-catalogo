package com.fullcycle.admin.catalogo.application.genre.retrieve.list;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultListGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsListGenre_shouldReturnGenres() {
        //given
        final var genres = List.of(
                Genre.newGenre("Ação", true),
                Genre.newGenre("Aventura", true)
        );
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var expectedItems = genres.stream()
                .map(GenreListOutput::from)
                .toList();
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, expectedTotal, genres);

        when(genreGateway.findAll(any())).thenReturn(expectedPagination);

        //when

        final var actualOutput = useCase.execute(aQuery);

        //then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(genreGateway, times(1)).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidCommand_whenCallsListGenreAndResultIsEmpty_shouldReturnGenres() {
        //given
        final var genres = List.<Genre>of();
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var expectedItems = List.<GenreListOutput>of();
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, expectedTotal, genres);

        when(genreGateway.findAll(any())).thenReturn(expectedPagination);

        //when

        final var actualOutput = useCase.execute(aQuery);

        //then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(genreGateway, times(1)).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidCommand_whenCallsListGenreAndRandomError_shouldReturnException() {
        //given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway error";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        when(genreGateway.findAll(any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        //when

        final var actualOutput = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(aQuery)
        );

        //then
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getMessage());

        verify(genreGateway, times(1)).findAll(eq(aQuery));
    }
}
