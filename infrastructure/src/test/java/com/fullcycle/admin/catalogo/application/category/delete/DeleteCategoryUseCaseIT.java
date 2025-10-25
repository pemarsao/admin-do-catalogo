package com.fullcycle.admin.catalogo.application.category.delete;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;

@IntegrationTest
public class DeleteCategoryUseCaseIT {

    @Autowired
    private DeleteCategoryUseCase useCase;
    @Autowired
    private CategoryRepository repository;
    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsDeleteCategory_shouldBeOk() {
        Category aCategory = Category.newCategory("Film", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();

        save(aCategory);

        Assertions.assertEquals(1, repository.count());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(0, repository.count());

    }

    @Test
    public void givenAInvalidCommand_whenCallsDeleteCategory_shouldBeOk() {
        final var expectedId = CategoryID.from("123");

        Assertions.assertEquals(0, repository.count());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(0, repository.count());

    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsException_shouldReturnException() {
        Category aCategory = Category.newCategory("Film", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();

        Mockito.doThrow(new IllegalStateException("Gateway Error")).when(categoryGateway).deleteById(Mockito.eq(expectedId));

        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        Mockito.verify(categoryGateway, Mockito.times(1)).deleteById(Mockito.eq(expectedId));

    }

    private void save(Category... aCategory) {
        repository.saveAllAndFlush(Arrays.stream(aCategory).map(CategoryJpaEntity::from).toList());
    }

}
