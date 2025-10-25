package com.fullcycle.admin.catalogo.application.genre.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DeleteGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsDeleteGenre_shouldBeOk() {
        //given
        final var aGenre = Genre.newGenre("Ação", true);
        doNothing().when(genreGateway).deleteById(any());

        //when
        useCase.execute(aGenre.getId().getValue());

        //then
        verify(genreGateway, times(1)).deleteById(eq(aGenre.getId()));
    }

    @Test
    public void givenAInvalidCommand_whenCallsDeleteGenre_shouldBeOk() {
        //given
        final var expectedId = GenreID.from("123");
        doNothing().when(genreGateway).deleteById(any());

        //when
        useCase.execute(expectedId.getValue());

        //then
        verify(genreGateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsException_shouldReturnException() {
        //given
        final var aGenre = Genre.newGenre("Ação", true);
        doThrow(new IllegalStateException("Gateway error")).when(genreGateway).deleteById(any());

        //when
        final var actualException = Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(aGenre.getId().getValue()));

        //then
        Assertions.assertEquals("Gateway error", actualException.getMessage());
        verify(genreGateway, times(1)).deleteById(eq(aGenre.getId()));
    }


}
