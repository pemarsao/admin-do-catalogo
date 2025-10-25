package com.fullcycle.admin.catalogo.domain.validation;

public abstract class Validator {

    private final ValidateHandler handler;

    protected Validator(final ValidateHandler handler) {
        this.handler = handler;
    }

    public abstract void validate();

    protected ValidateHandler validateHandler() {
        return this.handler;
    }

}
