package com.fullcycle.admin.catalogo.application.video.retrieve.list;

import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoPreview;

import java.time.Instant;
import java.util.Set;

public record VideosListOutput(
    String id,
    String title,
    String description,
    Instant createdAt,
    Instant updatedAt
) {
    public static VideosListOutput from(final VideoPreview aVideo) {
        return new VideosListOutput(
            aVideo.id(),
            aVideo.title(),
            aVideo.description(),
            aVideo.createdAt(),
            aVideo.updatedAt()
        );
    }
}
