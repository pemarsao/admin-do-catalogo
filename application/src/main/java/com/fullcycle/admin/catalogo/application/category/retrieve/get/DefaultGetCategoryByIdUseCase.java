package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.validation.Error;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetCategoryByIdUseCase extends GetCategoryByIdUseCase {

    public static final String CATEGORY_WITH_ID_WAS_NOT_FOUND = "Category with ID %s was not found";

    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }


    @Override
    public CategoryOutput execute(String anIn) {
        CategoryID anCategoryID = CategoryID.from(anIn);
        return this.categoryGateway.findById(anCategoryID)
            .map(CategoryOutput::from)
            .orElseThrow(notFound(anCategoryID));
    }

    private static Supplier<NotFoundException> notFound(CategoryID anId) {
        return () -> NotFoundException.with(Category.class, anId);
    }
}
