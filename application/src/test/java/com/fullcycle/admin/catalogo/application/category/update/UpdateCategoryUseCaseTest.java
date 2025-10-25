package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateCategoryUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var aCategory = Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedId = aCategory.getId();
        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);


        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));

        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(ArgumentMatchers.eq(expectedId));
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aUpdateCategory -> {
            return Objects.equals(expectedName, aUpdateCategory.getName()) &&
                   Objects.equals(expectedDescription, aUpdateCategory.getDescription()) &&
                   Objects.equals(expectedIsActive, aUpdateCategory.isActive()) &&
                   Objects.equals(expectedId, aUpdateCategory.getId()) &&
                   Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt()) &&
                   aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt()) &&
                   Objects.isNull(aUpdateCategory.getDeletedAt());
        }));
    }

    @Test
    public void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var aCategory = Category.newCategory("Film", null, true);

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        Mockito.when(categoryGateway.findById(Mockito.eq(aCategory.getId()))).thenReturn(Optional.of(aCategory.clone()));

        final var aCommand = UpdateCategoryCommand.with(aCategory.getId().getValue(), expectedName, expectedDescription, expectedIsActive);

        final var notification = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCInactiveCommandWithCategory_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var aCategory = Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var expectedId = aCategory.getId();
        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);


        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));

        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        Assertions.assertTrue(aCategory.isActive());
        Assertions.assertNull(aCategory.getDeletedAt());

        final var actualOutput = useCase.execute(aCommand).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(ArgumentMatchers.eq(expectedId));
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aUpdateCategory -> {
            return Objects.equals(expectedName, aUpdateCategory.getName()) &&
                   Objects.equals(expectedDescription, aUpdateCategory.getDescription()) &&
                   Objects.equals(expectedIsActive, aUpdateCategory.isActive()) &&
                   Objects.equals(expectedId, aUpdateCategory.getId()) &&
                   Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt()) &&
                   aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt()) &&
                   Objects.nonNull(aUpdateCategory.getDeletedAt());
        }));
    }

    @Test
    public void givenAValidCommand_whenCGatewayThrowsRandomException_shouldReturnAException() {

        final var aCategory = Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;

        final var expectedId = aCategory.getId();
        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));

        when(categoryGateway.update(any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        Notification notification = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1)).update(argThat(aUpdateCategory -> {
            return Objects.equals(expectedName, aUpdateCategory.getName()) &&
                   Objects.equals(expectedDescription, aUpdateCategory.getDescription()) &&
                   Objects.equals(expectedIsActive, aUpdateCategory.isActive()) &&
                   Objects.equals(expectedId, aUpdateCategory.getId()) &&
                   Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt()) &&
                   aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt()) &&
                   Objects.isNull(aUpdateCategory.getDeletedAt());
        }));
    }

    @Test
    public void givenACommandWithInvalidID_whenCallsUpdateCategory_shouldReturnNotFoundException() {

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedMessage = "Category with ID 123 was not found";
        final var expectedCount = 1;

        final var expectedId = "123";
        final var aCommand = UpdateCategoryCommand.with(expectedId, expectedName, expectedDescription, expectedIsActive);


        Mockito.when(categoryGateway.findById(Mockito.eq(CategoryID.from(expectedId)))).thenReturn(Optional.empty());

        final var actualException = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedMessage, actualException.getErrors().get(0).message());


        Mockito.verify(categoryGateway, Mockito.times(1)).findById(ArgumentMatchers.eq(CategoryID.from(expectedId)));
        Mockito.verify(categoryGateway, Mockito.times(0)).update(any());
    }

}
