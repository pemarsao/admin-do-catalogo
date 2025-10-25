package com.fullcycle.admin.catalogo.domain.genre;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GenreTest extends UnitTest {

    @Test
    public void givenAValidParams_whenCallNewGenre_thenInstantiateGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAInvalidNullName_whenCallNewGenreAndValidate_thenShouldReceiveError() {
        final String expectedName = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedIsActive = true;
        final var expectedError = Assertions.assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));
        Assertions.assertEquals(expectedErrorCount, expectedError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, expectedError.getErrors().get(0).message());
    }

    @Test
    public void givenAInvalidEmptyName_whenCallNewGenreAndValidate_thenShouldReceiveError() {
        final String expectedName = "    ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedIsActive = true;

        final var expectedError = Assertions.assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));

        Assertions.assertEquals(expectedErrorCount, expectedError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, expectedError.getErrors().get(0).message());
    }

    @Test
    public void givenAInvalidNameLengthGreaterThan255_whenCallNewGenreAndValidate_thenShouldReceiveError() {
        final String expectedName = """
            As experiências acumuladas demonstram que a necessidade de renovação processual maximiza as possibilidades por conta das direções preferenciais no sentido do progresso.
            Não obstante, o entendimento das metas propostas exige a precisão e a definição de alternativas específicas. Assim mesmo, o consenso sobre a 
            necessidade de qualificação facilita a criação de todos os recursos funcionais envolvidos.
            """;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 1 and 255 characters";
        final var expectedIsActive = true;
        final var expectedError = Assertions.assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));
        Assertions.assertEquals(expectedErrorCount, expectedError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, expectedError.getErrors().get(0).message());

    }

    @Test
    public void givenAnDeactivatedGenre_whenCallActivate_thenShouldBeActive() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var actualGenre = Genre.newGenre(expectedName, false);
        Assertions.assertNotNull(actualGenre);
        Assertions.assertFalse(actualGenre.isActive());
        Assertions.assertNotNull(actualGenre.getDeletedAt());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.activate();

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories().size());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(expectedUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnActivatedGenre_whenCallDeactivate_thenShouldBeInactive() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = 0;

        final var actualGenre = Genre.newGenre(expectedName, true);
        Assertions.assertNotNull(actualGenre);
        Assertions.assertTrue(actualGenre.isActive());
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.deactivate();

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories().size());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(expectedUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnDeactivatedGenre_whenUpdate_thenShouldBeGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));

        final var actualGenre = Genre.newGenre("acao", false);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertFalse(actualGenre.isActive());
        Assertions.assertNotNull(actualGenre.getDeletedAt());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.update(expectedName, expectedIsActive, expectedCategories);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(expectedUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnActiveGenre_whenUpdate_thenShouldBeGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.of(CategoryID.from("123"));

        final var actualGenre = Genre.newGenre("acao", true);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertTrue(actualGenre.isActive());
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.update(expectedName, expectedIsActive, expectedCategories);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(expectedUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAInvalidNullName_whenCallUpdateAndValidate_thenShouldReceiveNotificationException() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualGenre = Genre.newGenre("acao", true);

        final var expectedError = Assertions.assertThrows(NotificationException.class, () -> actualGenre.update(expectedName, expectedIsActive, expectedCategories));
        Assertions.assertEquals(expectedErrorCount, expectedError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, expectedError.getErrors().get(0).message());
    }

    @Test
    public void givenAInvalidEmptyName_whenCallUpdateAndValidate_thenShouldReceiveNotificationException() {
        final var expectedName = "     ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualGenre = Genre.newGenre("acao", true);

        final var expectedError = Assertions.assertThrows(NotificationException.class, () -> actualGenre.update(expectedName, expectedIsActive, expectedCategories));
        Assertions.assertEquals(expectedErrorCount, expectedError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, expectedError.getErrors().get(0).message());
    }

    @Test
    public void givenAInvalidEmptyName_whenCallUpdateWithNullCategories_thenShouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = new ArrayList<>();


        final var actualGenre = Genre.newGenre("acao", true);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertTrue(actualGenre.isActive());
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        final var expectedError = Assertions.assertDoesNotThrow(() -> actualGenre.update(expectedName, expectedIsActive, null));

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(expectedUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallAddCategory_thenShouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var movieID = CategoryID.from("123");
        final var seriesID = CategoryID.from("456");
        final List<CategoryID> expectedCategories = List.of(movieID, seriesID);

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(0, actualGenre.getCategories().size());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.addCategory(movieID);
        actualGenre.addCategory(seriesID);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(expectedUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallAddCategoryWithNull_thenShouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = new ArrayList<>();

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(0, actualGenre.getCategories().size());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.addCategory(null);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertEquals(expectedUpdatedAt,actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAValidTwoCategoriesGenre_whenCallRemoveCategory_thenShouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var movieID = CategoryID.from("123");
        final var seriesID = CategoryID.from("456");
        final List<CategoryID> expectedCategories = List.of(movieID);

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        actualGenre.update(expectedName, expectedIsActive, List.of(movieID, seriesID));

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(2, actualGenre.getCategories().size());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.removeCategory(seriesID);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(expectedUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAValidTwoCategoriesGenre_whenCallRemoveCategoryWithNull_thenShouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var movieID = CategoryID.from("123");
        final var seriesID = CategoryID.from("456");
        final List<CategoryID> expectedCategories = List.of(movieID, seriesID);

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        actualGenre.update(expectedName, expectedIsActive, expectedCategories);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(2, actualGenre.getCategories().size());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.removeCategory(null);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertEquals(expectedUpdatedAt, actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallAddAllCategory_thenShouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var movieID = CategoryID.from("123");
        final var seriesID = CategoryID.from("456");
        final List<CategoryID> expectedCategories = List.of(movieID, seriesID);

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(0, actualGenre.getCategories().size());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.addCategories(expectedCategories);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(expectedUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallAddAllCategoryWithNull_thenShouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = new ArrayList<>();

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(0, actualGenre.getCategories().size());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.addCategories(null);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertEquals(expectedUpdatedAt,actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallAddAllCategoryWithEmpty_thenShouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = new ArrayList<>();

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(0, actualGenre.getCategories().size());

        final var expectedCreatedAt = actualGenre.getCreatedAt();
        final var expectedUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.addCategories(null);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(expectedCreatedAt, actualGenre.getCreatedAt());
        Assertions.assertEquals(expectedUpdatedAt,actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

    }

}
