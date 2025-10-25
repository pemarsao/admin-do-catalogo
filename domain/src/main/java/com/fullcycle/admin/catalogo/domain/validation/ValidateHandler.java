package com.fullcycle.admin.catalogo.domain.validation;

import java.util.List;

public interface ValidateHandler {

    ValidateHandler append(Error anError);

    ValidateHandler append(ValidateHandler aHandler);

    <T> T validate(Validation<T> aValidation);

    List<Error> getErrors();
    default boolean hasError() {
        return getErrors() != null && !getErrors().isEmpty();
    }
    default Error firstError() {
        if (getErrors() != null && !getErrors().isEmpty()) {
            return getErrors().get(0);
        }
        return null;
    }

    public interface Validation<T> {
        T validate();
    }


}
