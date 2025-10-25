package com.fullcycle.admin.catalogo.domain.category;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Category extends AggregateRoot<CategoryID> implements Cloneable {

    private String name;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Category(final CategoryID id, final String name, final String description, final boolean active, final Instant createdAt, final Instant updatedAt, final Instant deletedAt) {
        super(id);
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updatedAt' should not be null");;
        this.deletedAt = deletedAt;
    }

    public static Category newCategory(final String name, final String description, final boolean active) {
        final var id = CategoryID.unique();
        final var now = InstantUtils.now();
        final var deletedAt = active ? null : now;
        return new Category(id, name, description, active, now, now, deletedAt);
    }

    public static Category with(Category aCategory) {
        return with(aCategory.getId(),
            aCategory.getName(),
            aCategory.getDescription(),
            aCategory.isActive(),
            aCategory.getCreatedAt(),
            aCategory.getUpdatedAt(),
            aCategory.getDeletedAt());
    }

    public static Category with(final CategoryID aId,
                                final String name,
                                final String description,
                                final Boolean active,
                                final Instant createdAt,
                                final Instant updatedAt,
                                final Instant deletedAt) {
        return new Category(aId, name, description, active, createdAt, updatedAt, deletedAt);
    }

    @Override
    public void validate(final ValidateHandler handler) {
        new CategoryValidator(this, handler).validate();
    }

    public Category deactivate() {
        if (getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }
        this.active = false;
        this.updatedAt = InstantUtils.now();

        return this;
    }

    public Category activate() {
        this.active = true;
        this.updatedAt = InstantUtils.now();
        this.deletedAt = null;

        return this;
    }

    public Category update(final String name, final String description, boolean isActive) {
        if (isActive) {
            activate();
        } else {
            deactivate();
        }
        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
        return this;
    }

    @Override
    public Category clone()  {
        try {
            return (Category) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
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
}
