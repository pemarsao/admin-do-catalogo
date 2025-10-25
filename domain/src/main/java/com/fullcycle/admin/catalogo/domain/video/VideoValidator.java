package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;
import com.fullcycle.admin.catalogo.domain.validation.Validator;

public class VideoValidator extends Validator {
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int DESCRIPTION_MAX_LENGTH = 4_000;
    private final Video video;

    public VideoValidator(final Video video, final ValidateHandler handler) {
        super(handler);
        this.video = video;
    }

    @Override
    public void validate() {
        checkTitleConstraints();
        checkDescriptionConstraints();
        checkLaunchedAtConstraints();
        checkRatingConstraints();
    }

    private void checkTitleConstraints() {
        final var title = this.video.getTitle();
        if (title == null) {
            this.validateHandler().append(new Error("'title' should not be null"));
            return;
        }

        if (title.isBlank()) {
            this.validateHandler().append(new Error("'title' should not be empty"));
            return;
        }

        final int length = title.trim().length();
        if (length > TITLE_MAX_LENGTH) {
            this.validateHandler().append(new Error("'title' must be between 1 and 255 characters"));
        }
    }

    private void checkDescriptionConstraints() {
        final var description = this.video.getDescription();
        if (description == null) {
            this.validateHandler().append(new Error("'description' should not be null"));
            return;
        }

        if (description.isBlank()) {
            this.validateHandler().append(new Error("'description' should not be empty"));
            return;
        }

        final int length = description.trim().length();
        if (length > DESCRIPTION_MAX_LENGTH) {
            this.validateHandler().append(new Error("'description' must be between 1 and 4000 characters"));
        }
    }

    private void checkLaunchedAtConstraints() {
        if (this.video.getLaunchedAt() == null) {
            this.validateHandler().append(new Error("'launchedAt' should not be null"));
        }
    }

    private void checkRatingConstraints() {
        if (this.video.getRating() == null) {
            this.validateHandler().append(new Error("'rating' should not be null"));
        }
    }
}
