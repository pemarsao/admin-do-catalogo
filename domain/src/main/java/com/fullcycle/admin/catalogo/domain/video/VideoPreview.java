package com.fullcycle.admin.catalogo.domain.video;

import java.time.Instant;

public record VideoPreview(
    String id,
    String title,
    String description,
    Instant createdAt,
    Instant updatedAt
) {

    public static VideoPreview from(Video aVideo) {
        return new VideoPreview(
            aVideo.getId().getValue(),
            aVideo.getTitle(),
            aVideo.getDescription(),
            aVideo.getCreatedAt(),
            aVideo.getUpdatedAt()
        );
    }

}
