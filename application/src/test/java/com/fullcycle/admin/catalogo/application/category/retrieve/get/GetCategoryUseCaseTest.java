package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class GetCategoryUseCaseTest extends UseCaseTest {


    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }


    @Test
    public void givenAValidCommand_whenCallsGetCategory_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));

        final var actualCategory = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedId, actualCategory.id());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.createdAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.updatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.deletedAt());

    }


    @Test
    public void givenAInvalidCommand_whenCallsGetCategory_shouldReturnNotFound() {
        final var expectedId = CategoryID.from("123");
        final var expectedMessage = "Category with ID 123 was not found";
        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.empty());

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedMessage, actualException.getMessage());
    }


    @Test
    public void givenAValidCommand_whenGatewayThrowsException_shouldReturnException() {
        final var expectedId = CategoryID.from("123");
        final var expectedMessage = "Gateway Error";
        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenThrow(new IllegalStateException(expectedMessage));

        final var actualException = Assertions.assertThrows(
            IllegalStateException.class, () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedMessage, actualException.getMessage());
    }

}
