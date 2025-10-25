package com.fullcycle.admin.catalogo.application.video.retrieve.get;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.ImageMedia;
import com.fullcycle.admin.catalogo.domain.video.Rating;
import com.fullcycle.admin.catalogo.domain.video.Video;

import java.time.Instant;
import java.util.Set;

public record VideoOutput(
    String id,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    int launchAt,
    double duration,
    Rating rating,
    boolean opened,
    boolean published,
    Set<String> categories,
    Set<String> castMembers,
    Set<String> genres,
    ImageMedia banner,
    ImageMedia thumbnail,
    ImageMedia thumbnailHalf,
    AudioVideoMedia video,
    AudioVideoMedia trailer
) {

    public static VideoOutput from(final Video aVideo) {
        return new VideoOutput(
            aVideo.getId().getValue(),
            aVideo.getCreatedAt(),
            aVideo.getUpdatedAt(),
            aVideo.getTitle(),
            aVideo.getDescription(),
            aVideo.getLaunchedAt().getValue(),
            aVideo.getDuration(),
            aVideo.getRating(),
            aVideo.getOpened(),
            aVideo.getPublished(),
            CollectionUtils.mapTo(aVideo.getCategories(), Identifier::getValue),
            CollectionUtils.mapTo(aVideo.getCastMembers(), Identifier::getValue),
            CollectionUtils.mapTo(aVideo.getGenres(), Identifier::getValue),
            aVideo.getBanner().orElse(null),
            aVideo.getThumbnail().orElse(null),
            aVideo.getThumbnailHalf().orElse(null),
            aVideo.getVideo().orElse(null),
            aVideo.getTrailer().orElse(null)
        );
    }

}
