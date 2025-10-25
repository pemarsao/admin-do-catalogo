package com.fullcycle.admin.catalogo.domain.castmember;

import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;
import com.fullcycle.admin.catalogo.domain.validation.Validator;

public class CastMemberValidator extends Validator {

    private static final int NAME_MAX_LENGTH = 255;
    private static final int NAME_MIN_LENGTH = 1;
    private final CastMember castMember;

    protected CastMemberValidator(final CastMember castMember, final ValidateHandler handler) {
        super(handler);
        this.castMember = castMember;
    }

    @Override
    public void validate() {
        checkNameConstraints();
        checkTypeConstraints();
    }

    private void checkNameConstraints() {
        final var name = this.castMember.getName();
        if (name == null) {
            this.validateHandler().append(new Error("'name' should not be null"));
            return;
        }

        if (name.isBlank()) {
            this.validateHandler().append(new Error("'name' should not be empty"));
            return;
        }

        final int length = name.trim().length();
        if (length > NAME_MAX_LENGTH || length < NAME_MIN_LENGTH) {
            this.validateHandler().append(new Error("'name' must be between 1 and 255 characters"));
        }
    }

    private void checkTypeConstraints() {
        final var type = this.castMember.getType();
        if (type == null) {
            this.validateHandler().append(new Error("'type' should not be null"));
        }
    }
}
