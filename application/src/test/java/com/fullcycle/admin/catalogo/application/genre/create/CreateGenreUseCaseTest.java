package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


public class CreateGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    DefaultCreateGenreUseCase useCase;

    @Mock
    CategoryGateway categoryGateway;

    @Mock
    GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway, genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreID() {

        // given
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(command);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(genreGateway, times(1)).create(argThat(aGenre ->
            Objects.equals(expectedName, aGenre.getName())
            && Objects.equals(expectedIsActive, aGenre.isActive())
            && Objects.equals(expectedCategories, aGenre.getCategories())
            && Objects.nonNull(aGenre.getId())
            && Objects.nonNull(aGenre.getCreatedAt())
            && Objects.nonNull(aGenre.getUpdatedAt())
            && Objects.isNull(aGenre.getDeletedAt())
        ));

    }

    @Test
    public void givenAValidCommandAndInactive_whenCallsCreateGenre_shouldReturnGenreID() {

        // given
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));
        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());
        // when
        final var actualOutput = useCase.execute(command);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(genreGateway, times(1)).create(argThat(aGenre ->
            Objects.equals(expectedName, aGenre.getName())
            && Objects.equals(expectedIsActive, aGenre.isActive())
            && Objects.equals(expectedCategories, aGenre.getCategories())
            && Objects.nonNull(aGenre.getId())
            && Objects.nonNull(aGenre.getCreatedAt())
            && Objects.nonNull(aGenre.getUpdatedAt())
            && Objects.nonNull(aGenre.getDeletedAt())
        ));

    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsCreateGenre_shouldReturnGenreID() {

        // given
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            CategoryID.from("123"),
            CategoryID.from("456")
        );

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());
        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        // when
        final var actualOutput = useCase.execute(command);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(genreGateway, times(1)).create(argThat(aGenre ->
            Objects.equals(expectedName, aGenre.getName())
            && Objects.equals(expectedIsActive, aGenre.isActive())
            && Objects.equals(expectedCategories, aGenre.getCategories())
            && Objects.nonNull(aGenre.getId())
            && Objects.nonNull(aGenre.getCreatedAt())
            && Objects.nonNull(aGenre.getUpdatedAt())
            && Objects.isNull(aGenre.getDeletedAt())
        ));
    }

    @Test
    public void givenAInvalidEmptyName_whenCallsCreateGenre_thenShouldReturnDomainException() {
        // given
        final var expectedName = "  ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            CategoryID.from("123"),
            CategoryID.from("456")
        );
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(command));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(genreGateway, times(0)).create(any());
        Mockito.verify(categoryGateway, times(1)).existsByIds(any());
    }

    @Test
    public void givenAInvalidNullName_whenCallsCreateGenre_thenShouldReturnDomainException() {
        // given
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            CategoryID.from("123"),
            CategoryID.from("456")
        );
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(command));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(genreGateway, times(0)).create(any());
        Mockito.verify(categoryGateway, times(1)).existsByIds(any());
    }

    @Test
    public void givenAValidCommand_whenCallsCreateGenreAndSomeCategoryDoesNotExists_thenShouldReturnDomainException() {
        // given
        final var series = CategoryID.from("123");
        final var filmes = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            series,
            filmes,
            documentarios
        );
        final var expectedErrorMessage = "Some categories could not be found: 456, 789";
        final var expectedErrorCount = 1;

        when(categoryGateway.existsByIds(any())).thenReturn(List.of(series));

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(command));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(genreGateway, times(0)).create(any());
        Mockito.verify(categoryGateway, times(1)).existsByIds(any());
    }

    @Test
    public void givenAInvalidName_whenCallsCreateGenreAndSomeCategoryDoesNotExists_thenShouldReturnDomainException() {
        // given
        final var series = CategoryID.from("123");
        final var filmes = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            series,
            filmes,
            documentarios
        );
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be empty";
        final var expectedErrorCount = 2;

        when(categoryGateway.existsByIds(any())).thenReturn(List.of(series));

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(command));

        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, actualException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, actualException.getErrors().get(1).message());

        Mockito.verify(genreGateway, times(0)).create(any());
        Mockito.verify(categoryGateway, times(1)).existsByIds(any());
    }

}
