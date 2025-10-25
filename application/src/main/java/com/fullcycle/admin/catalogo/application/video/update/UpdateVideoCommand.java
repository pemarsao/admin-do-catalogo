package com.fullcycle.admin.catalogo.application.video.update;

import com.fullcycle.admin.catalogo.domain.resource.Resource;

import java.util.Optional;
import java.util.Set;

public record UpdateVideoCommand(
    String id,
    String title,
    String description,
    Integer launchAt,
    Double duration,
    String rating,
    Boolean opened,
    Boolean published,
    Set<String> categories,
    Set<String> members,
    Set<String> genres,
    Resource video,
    Resource trailer,
    Resource banner,
    Resource thumbnail,
    Resource thumbnailHalf
) {


    public Optional<Resource> getVideo() {
        return Optional.ofNullable(video);
    }

    public Optional<Resource> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public Optional<Resource> getBanner() {
        return Optional.ofNullable(banner);
    }

    public Optional<Resource> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public Optional<Resource> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public static UpdateVideoCommand with(
        final String id,
        final String title,
        final String description,
        final Integer launchAt,
        final Double duration,
        final String rating,
        final Boolean opened,
        final Boolean published,
        final Set<String> categories,
        final Set<String> members,
        final Set<String> genres,
        final Resource video,
        final Resource trailer,
        final Resource banner,
        final Resource thumbnail,
        final Resource thumbnailHalf
    ) {
        return new UpdateVideoCommand(
            id,
            title,
            description,
            launchAt,
            duration,
            rating,
            opened,
            published,
            categories,
            members,
            genres,
            video,
            trailer,
            banner,
            thumbnail,
            thumbnailHalf
        );
    }

    public static UpdateVideoCommand with(
        final String id,
        final String title,
        final String description,
        final Integer launchAt,
        final Double duration,
        final String rating,
        final Boolean opened,
        final Boolean published,
        final Set<String> categories,
        final Set<String> members,
        final Set<String> genres
    ) {
        return with(
            id,
            title,
            description,
            launchAt,
            duration,
            rating,
            opened,
            published,
            categories,
            members,
            genres,
            null,
            null,
            null,
            null,
            null
        );
    }
}
