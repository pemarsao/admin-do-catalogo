package com.fullcycle.admin.catalogo.infrastructure.category.persistence;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@MySQLGatewayTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    public void given_AInvalidNullName_whenCallsSave_shouldReturnError() {

        final var actualMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.name";
        final var actualPropertyName = "name";
        Category aCategory = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var anEntity = CategoryJpaEntity.from(aCategory);
        anEntity.setName(null);
        DataIntegrityViolationException actualException =
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> repository.save(anEntity));

        final PropertyValueException actualCause =
            Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(actualPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(actualMessage, actualCause.getMessage());

    }

    @Test
    public void given_AInvalidNullCreatedAt_whenCallsSave_shouldReturnError() {

        final var actualMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.createdAt";
        final var actualPropertyName = "createdAt";
        Category aCategory = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var anEntity = CategoryJpaEntity.from(aCategory);
        anEntity.setCreatedAt(null);
        DataIntegrityViolationException actualException =
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> repository.save(anEntity));

        final PropertyValueException actualCause =
            Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(actualPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(actualMessage, actualCause.getMessage());

    }

    @Test
    public void given_AInvalidNullUpdatedAt_whenCallsSave_shouldReturnError() {

        final var actualMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";
        final var actualPropertyName = "updatedAt";
        Category aCategory = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var anEntity = CategoryJpaEntity.from(aCategory);
        anEntity.setUpdatedAt(null);
        DataIntegrityViolationException actualException =
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> repository.save(anEntity));

        final PropertyValueException actualCause =
            Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(actualPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(actualMessage, actualCause.getMessage());

    }
}
