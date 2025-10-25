package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;

@IntegrationTest
public class GetCategoryUseCaseIT {

    @Autowired
    private GetCategoryByIdUseCase useCase;

    @Autowired
    private CategoryRepository repository;
    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsGetCategory_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        save(aCategory);

        final var actualCategory = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedId, actualCategory.id());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.createdAt());
        Assertions.assertNotNull(actualCategory.updatedAt());
        Assertions.assertNull(actualCategory.deletedAt());

    }

    @Test
    public void givenAInvalidCommand_whenCallsGetCategory_shouldReturnNotFound() {
        final var expectedId = CategoryID.from("123");
        final var expectedMessage = "Category with ID 123 was not found";

        final var actualException = Assertions.assertThrows(
            NotFoundException.class, () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedMessage, actualException.getMessage());
    }


    @Test
    public void givenAValidCommand_whenGatewayThrowsException_shouldReturnException() {
        final var expectedId = CategoryID.from("123");
        final var expectedMessage = "Gateway Error";

        Mockito.doThrow(new IllegalStateException(expectedMessage)).when(categoryGateway).findById(Mockito.eq(expectedId));

        final var actualException = Assertions.assertThrows(
            IllegalStateException.class, () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedMessage, actualException.getMessage());
    }

    private void save(Category... aCategory) {
        repository.saveAllAndFlush(Arrays.stream(aCategory).map(CategoryJpaEntity::from).toList());
    }

}
