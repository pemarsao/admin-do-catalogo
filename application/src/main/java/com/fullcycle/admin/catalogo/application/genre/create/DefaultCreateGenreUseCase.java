package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase {

    private final CategoryGateway categoryGateway;

    private final GenreGateway genreGateway;

    public DefaultCreateGenreUseCase(CategoryGateway categoryGateway, GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand anIn) {
        final var name = anIn.name();
        final var isActive = anIn.isActive();
        final var categories = toCategoriesIDs(anIn.categories());

        final var notification = Notification.create();
        notification.append(validateCategories(categories));

        final var aGenre = notification.validate(() -> Genre.newGenre(name, isActive));

        if (notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Genre", notification);
        }

        aGenre.addCategories(categories);

        return  CreateGenreOutput.from(this.genreGateway.create(aGenre));
    }

    private ValidateHandler validateCategories(List<CategoryID> ids) {
        final var notification = Notification.create();
        if (ids == null || ids.isEmpty()) {
            return notification;
        }

        final var retrievedIds = this.categoryGateway.existsByIds(ids);
        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);

            final var missingIdsMessage = missingIds.stream()
                .map(CategoryID::getValue)
                .collect(Collectors.joining(", "));

            notification.append(new Error("Some categories could not be found: %s".formatted(missingIdsMessage)));
        }

        return notification;
    }

    private List<CategoryID> toCategoriesIDs(List<String> categories) {
        return categories.stream()
            .map(CategoryID::from)
            .toList();

    }
}
