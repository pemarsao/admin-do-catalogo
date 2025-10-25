package com.fullcycle.admin.catalogo.application.genre.delete;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class DeleteGenreUseCaseIT {

    @Autowired
    private DeleteGenreUseCase useCase;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;


    @Test
    public void givenAValidCommand_whenCallsDeleteGenre_shouldBeOk() {
        //given
        final var aGenre = genreGateway.create(Genre.newGenre("Ação", true));
        Assertions.assertEquals(1, genreRepository.count());
        //when
        useCase.execute(aGenre.getId().getValue());

        //then
        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenAInvalidCommand_whenCallsDeleteGenre_shouldBeOk() {
        //given
        final var aGenre = genreGateway.create(Genre.newGenre("Ação", true));
        Assertions.assertEquals(1, genreRepository.count());
        //when
        final var expectedId = GenreID.from("123");
        useCase.execute(expectedId.getValue());
        //then
        Assertions.assertEquals(1, genreRepository.count());
    }

}
