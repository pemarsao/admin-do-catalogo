package com.fullcycle.admin.catalogo.application.category.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class DeleteCategoryUseCaseTest extends UseCaseTest {


    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsDeleteCategory_shouldBeOk() {
        Category aCategory = Category.newCategory("Film", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();

        Mockito.doNothing().when(categoryGateway).deleteById(Mockito.eq(expectedId));

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Mockito.verify(categoryGateway, Mockito.times(1)).deleteById(Mockito.eq(expectedId));

    }

    @Test
    public void givenAInvalidCommand_whenCallsDeleteCategory_shouldBeOk() {
        final var expectedId = CategoryID.from("123");

        Mockito.doNothing().when(categoryGateway).deleteById(Mockito.eq(expectedId));

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Mockito.verify(categoryGateway, Mockito.times(1)).deleteById(Mockito.eq(expectedId));

    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsException_shouldReturnException() {
        Category aCategory = Category.newCategory("Film", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();

        Mockito.doThrow(new IllegalStateException("Gateway Error")).when(categoryGateway).deleteById(Mockito.eq(expectedId));

        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        Mockito.verify(categoryGateway, Mockito.times(1)).deleteById(Mockito.eq(expectedId));

    }

}
