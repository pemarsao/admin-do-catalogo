package com.fullcycle.admin.catalogo.application.genre.update;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UpdateGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    DefaultUpdateGenreUseCase useCase;
    @Mock
    CategoryGateway categoryGateway;
    @Mock
    GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway, genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreId() {

        // given
        final var aGenre = Genre.newGenre("acao", true);
        final var expectedId = aGenre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aCommand = UpdateGenreCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedIsActive,
            asString(expectedCategories)
        );

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(aGenre)));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        Mockito.verify(genreGateway, times(1)).update(argThat(aUpdated ->
            Objects.equals(expectedId, aUpdated.getId())
                && Objects.equals(expectedName, aUpdated.getName())
                && Objects.equals(expectedIsActive, aUpdated.isActive())
                && Objects.equals(expectedCategories, aUpdated.getCategories())
                && Objects.equals(aGenre.getCreatedAt(), aUpdated.getCreatedAt())
                && aGenre.getUpdatedAt().isBefore(aUpdated.getUpdatedAt())
                && Objects.isNull(aUpdated.getDeletedAt())
        ));

    }

    @Test
    public void givenAInactiveCommand_whenCallsUpdateGenre_shouldReturnGenreId() {

        // given
        final var aGenre = Genre.newGenre("acao", true);
        final var expectedId = aGenre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var aCommand = UpdateGenreCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedIsActive,
            asString(expectedCategories)
        );

        Assertions.assertTrue(aGenre.isActive());
        Assertions.assertNull(aGenre.getDeletedAt());

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(aGenre)));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        Mockito.verify(genreGateway, times(1)).update(argThat(aUpdated ->
            Objects.equals(expectedId, aUpdated.getId())
            && Objects.equals(expectedName, aUpdated.getName())
            && Objects.equals(expectedIsActive, aUpdated.isActive())
            && Objects.equals(expectedCategories, aUpdated.getCategories())
            && Objects.equals(aGenre.getCreatedAt(), aUpdated.getCreatedAt())
            && aGenre.getUpdatedAt().isBefore(aUpdated.getUpdatedAt())
            && Objects.nonNull(aUpdated.getDeletedAt())
        ));

    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsUpdateGenre_shouldReturnGenreId() {

        // given
        final var aGenre = Genre.newGenre("acao", true);
        final var expectedId = aGenre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            CategoryID.from("123"),
            CategoryID.from("456")
        );

        final var aCommand = UpdateGenreCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedIsActive,
            asString(expectedCategories)
        );

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(aGenre)));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());
        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        Mockito.verify(genreGateway, times(1)).update(argThat(aUpdated ->
            Objects.equals(expectedId, aUpdated.getId())
            && Objects.equals(expectedName, aUpdated.getName())
            && Objects.equals(expectedIsActive, aUpdated.isActive())
            && Objects.equals(expectedCategories, aUpdated.getCategories())
            && Objects.equals(aGenre.getCreatedAt(), aUpdated.getCreatedAt())
            && aGenre.getUpdatedAt().isBefore(aUpdated.getUpdatedAt())
            && Objects.isNull(aUpdated.getDeletedAt())
        ));

    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateGenre_shouldReturnNotificationException() {

        // given
        final var aGenre = Genre.newGenre("acao", true);
        final var expectedId = aGenre.getId();
        final var expectedName = "  ";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var aCommand = UpdateGenreCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedIsActive,
            asString(expectedCategories)
        );

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(aGenre)));

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(genreGateway, times(0)).update(any());

    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateGenreAndSomeCategoriesNotExists_shouldReturnNotificationException() {

        // given
        final var aGenre = Genre.newGenre("acao", true);
        final var filmes = CategoryID.from("123");
        final var series = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");
        final var expectedId = aGenre.getId();
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes, series, documentarios);
        final var expectedErrorCount = 2;
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be null";

        final var aCommand = UpdateGenreCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedIsActive,
            asString(expectedCategories)
        );

        when(categoryGateway.existsByIds(any())).thenReturn(List.of(filmes));
        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(aGenre)));

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, actualException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, actualException.getErrors().get(1).message());

        Mockito.verify(genreGateway, times(0)).update(any());

    }

}
