package com.fullcycle.admin.catalogo.application.genre.update;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultUpdateGenreUseCase extends UpdateGenreUseCase {

    public static final String CATEGORY_WITH_ID_WAS_NOT_FOUND = "Genre with ID %s was not found";

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public DefaultUpdateGenreUseCase(final CategoryGateway categoryGateway, final GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }


    @Override
    public UpdateGenreOutput execute(final UpdateGenreCommand anIn) {
        final var anId = GenreID.from(anIn.id());
        final var name = anIn.name();
        final var isActive = anIn.isActive();
        final var categories = toCategoriesIDs(anIn.categories());

        final var aGenre = genreGateway.findById(anId).orElseThrow(notFound(anId));

        final var notification = Notification.create();
        notification.append(validateCategories(categories));
        notification.validate(() -> aGenre.update(name, isActive, categories));

        if (notification.hasError()) {
            throw new NotificationException("Could not update Aggregate Genre", notification);
        }
        return UpdateGenreOutput.from(this.genreGateway.update(aGenre));
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

    private List<CategoryID> toCategoriesIDs(List<String> ids) {
        return ids.stream()
            .map(CategoryID::from)
            .toList();
    }

    private static Supplier<DomainException> notFound(Identifier anId) {
        return () -> DomainException.with(new Error(CATEGORY_WITH_ID_WAS_NOT_FOUND.formatted(anId.getValue())));
    }
}
