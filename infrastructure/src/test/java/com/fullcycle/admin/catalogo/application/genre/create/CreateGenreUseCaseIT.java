package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@IntegrationTest
public class CreateGenreUseCaseIT {

    @Autowired
    private CreateGenreUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreID() {

        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));

        // given
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        // when
        final var actualOutput = useCase.execute(command);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualGenre = this.genreRepository.findById(actualOutput.id()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
            expectedCategories.size() ==  actualGenre.getCategories().size()
            && expectedCategories.containsAll(actualGenre.getCategoriesIds())
        );
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAValidCommandAndInactive_whenCallsCreateGenre_shouldReturnGenreID() {

        // given
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        // when
        final var actualOutput = useCase.execute(command);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualGenre = this.genreRepository.findById(actualOutput.id()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
            expectedCategories.size() ==  actualGenre.getCategories().size()
            && expectedCategories.containsAll(actualGenre.getCategoriesIds())
        );
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNotNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsCreateGenre_shouldReturnGenreID() {

        // given

        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            series.getId(),
            filmes.getId()
        );

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        // when
        final var actualOutput = useCase.execute(command);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualGenre = this.genreRepository.findById(actualOutput.id()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
            expectedCategories.size() ==  actualGenre.getCategories().size()
            && expectedCategories.containsAll(actualGenre.getCategoriesIds())
        );
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAInvalidEmptyName_whenCallsCreateGenre_thenShouldReturnDomainException() {
        // given
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));

        final var expectedName = "  ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            series.getId(),
            filmes.getId()
        );
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

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
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));

        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            series.getId(),
            filmes.getId()
        );
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

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
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));
        final var filmes = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
            series.getId(),
            filmes,
            documentarios
        );
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be empty";
        final var expectedErrorCount = 2;

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

    private List<String> asString(final List<CategoryID> aCategories) {
        return aCategories.stream().map(CategoryID::getValue).toList();
    }

}
