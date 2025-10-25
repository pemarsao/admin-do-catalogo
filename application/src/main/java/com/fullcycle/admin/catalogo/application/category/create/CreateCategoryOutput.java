package com.fullcycle.admin.catalogo.application.category.create;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;

public record CreateCategoryOutput(String id) {
    public static CreateCategoryOutput from(final Category aCategory) {
        return CreateCategoryOutput.from(aCategory.getId().getValue());
    }

    public static CreateCategoryOutput from(final String anId) {
        return new CreateCategoryOutput(anId);
    }
}
