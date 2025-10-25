package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.event.DomainEvent;

import java.time.Instant;

public record VideoMediaCreated(
    String resourceId,
    String filePath,
    Instant occurredOn
) implements DomainEvent {

    public VideoMediaCreated(String resourceId, String filePath) {
        this(resourceId, filePath, Instant.now());
    }

}
