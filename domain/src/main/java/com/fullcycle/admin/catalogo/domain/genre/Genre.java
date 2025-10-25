package com.fullcycle.admin.catalogo.domain.genre;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Genre extends AggregateRoot<GenreID> {

    private String name;
    private boolean active;
    private List<CategoryID> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    protected Genre(final GenreID id,
                    final String name,
                    final boolean active,
                    final List<CategoryID> categories,
                    final Instant createdAt,
                    final Instant updatedAt,
                    final Instant deletedAt) {
        super(id);
        this.name = name;
        this.active = active;
        this.categories = categories;
        this.createdAt = Objects.requireNonNull(createdAt, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updatedAt' should not be null");
        this.deletedAt = deletedAt;

        selfValidate();

    }

    @Override
    public void validate(final ValidateHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    public static Genre newGenre(final String name, final boolean active) {
        final var id = GenreID.unique();
        final var now = InstantUtils.now();
        final var deletedAt = active ? null : now;
        return new Genre(id, name, active, new ArrayList<>(), now, now, deletedAt);
    }

    public static Genre with(final GenreID aId,
                            final String name,
                            final boolean active,
                            final List<CategoryID> categories,
                            final Instant createdAt,
                            final Instant updatedAt,
                            final Instant deletedAt) {
        return new Genre(aId, name, active, new ArrayList<>(categories), createdAt, updatedAt, deletedAt);
    }

    public static Genre with(final Genre aGenre) {
        return with(aGenre.getId(),
            aGenre.getName(),
            aGenre.isActive(),
            aGenre.getCategories(),
            aGenre.getCreatedAt(),
            aGenre.getUpdatedAt(),
            aGenre.getDeletedAt());
    }

    public Genre update(final String name, final boolean active, final List<CategoryID> categories) {
        if (active) {
            activate();
        } else {
            deactivate();
        }
        this.name = name;
        this.categories = new ArrayList<>(categories == null ? new ArrayList<>() : categories);
        this.updatedAt = InstantUtils.now();
        selfValidate();
        return this;
    }

    public void deactivate() {
        if (this.deletedAt == null) {
            this.deletedAt = InstantUtils.now();
        }
        this.active = false;
        this.updatedAt = InstantUtils.now();
    }

    public void activate() {
        this.deletedAt = null;
        this.active = true;
        this.updatedAt = InstantUtils.now();
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public Genre addCategory(CategoryID aCategory) {
        if (aCategory == null) {
            return this;
        }
        this.categories.add(aCategory);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre removeCategory(CategoryID aCategory) {
        if (aCategory == null) {
            return this;
        }
        this.categories.remove(aCategory);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre addCategories(List<CategoryID> categories) {
        if (categories == null) {
            return this;
        }
        this.categories.addAll(categories);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("Failed to create a Aggregate Genre", notification);
        }
    }
}
