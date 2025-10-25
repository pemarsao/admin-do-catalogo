package com.fullcycle.admin.catalogo.domain.exceptions;

import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

public class NotificationException extends DomainException{

    public NotificationException(final String aMessage, final Notification anErrors) {
        super(aMessage, anErrors.getErrors());
    }
}
