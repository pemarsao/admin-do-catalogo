package com.fullcycle.admin.catalogo.application.genre.retrieve.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetGenreByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetGenreByIdUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsGetGenreById_shouldReturnGenre() {
        //given
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            CategoryID.from("123"),
            CategoryID.from("456")
        );
        final var aGenre = Genre.newGenre(expectedName, expectedIsActive).addCategories(expectedCategories);

        when(genreGateway.findById(eq(aGenre.getId()))).thenReturn(Optional.of(aGenre));

        //when
        final var actualGenre = useCase.execute(aGenre.getId().getValue());

        //then
        Assertions.assertEquals(expectedName, actualGenre.name());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(asString(expectedCategories), actualGenre.categories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.createdAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.updatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.deletedAt());

        verify(genreGateway, times(1)).findById(eq(aGenre.getId()));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsException_shouldReturnNotFoundException() {
        //given
        final var expectedId = GenreID.from("123");
        final var expectedErrorMessage = "Genre with ID 123 was not found";

        when(genreGateway.findById(eq(expectedId))).thenReturn(Optional.empty());

        //when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        //then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        verify(genreGateway, times(1)).findById(eq(expectedId));
    }


}
