package com.fullcycle.admin.catalogo.application.video.create;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.InternalErrorException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.Rating;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.fullcycle.admin.catalogo.domain.video.VideoResource;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultCreateVideoUseCase extends CreateVideoUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;
    private final CastMemberGateway castMemberGateway;
    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultCreateVideoUseCase(CategoryGateway categoryGateway,
                                     GenreGateway genreGateway,
                                     CastMemberGateway castMemberGateway,
                                     VideoGateway videoGateway, MediaResourceGateway mediaResourceGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public CreateVideoOutput execute(CreateVideoCommand anIn) {
        final var aRating = Rating.of(anIn.rating()).orElse(null);
        final var launchAt = anIn.launchAt() != null ? Year.of(anIn.launchAt()) : null;
        final var categories = toIdentifiers(anIn.categories(), CategoryID::from);
        final var genres = toIdentifiers(anIn.genres(), GenreID::from);
        final var members = toIdentifiers(anIn.members(), CastMemberID::from);

        final var notification = Notification.create();
        notification.append(validateCategories(categories));
        notification.append(validateGenres(genres));
        notification.append(validateCastMembers(members));

        final var aVideo = Video.newVideo(
            anIn.title(),
            anIn.description(),
            launchAt,
            anIn.duration(),
            aRating,
            anIn.opened(),
            anIn.published(),
            categories,
            genres,
            members
        );

        aVideo.validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Video", notification);
        }
        return CreateVideoOutput.from(create(anIn, aVideo));
    }

    private Video create(final CreateVideoCommand anIn, final Video aVideo) {
        final var anId = aVideo.getId();
        try {
            final var mediaVideo = anIn.getVideo().map(it -> mediaResourceGateway.storeAudioVideo(anId, VideoResource.with(it, VideoMediaType.VIDEO))).orElse(null);
            final var mediaTrailer = anIn.getTrailer().map(it -> mediaResourceGateway.storeAudioVideo(anId, VideoResource.with(it, VideoMediaType.TRAILER))).orElse(null);
            final var imageBanner = anIn.getBanner().map(it -> mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.BANNER))).orElse(null);
            final var imageThumbnail = anIn.getThumbnail().map(it -> mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.THUMBNAIL))).orElse(null);
            final var imageThumbnailHalf = anIn.getThumbnailHalf().map(it -> mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.THUMBNAIL_HALF))).orElse(null);
            return this.videoGateway.create(aVideo.updateVideoMedia(mediaVideo)
                .updateTrailerMedia(mediaTrailer)
                .updateBannerMedia(imageBanner)
                .updateThumbnailMedia(imageThumbnail)
                .updateThumbnailHalfMedia(imageThumbnailHalf));
        } catch (final Throwable t){
            this.mediaResourceGateway.clearResources(anId);
            throw InternalErrorException.with("An error occurred while creating video [VideoID: %s]".formatted(anId.getValue()), t);
        }
    }

    private ValidateHandler validateCategories(final Set<CategoryID> categories) {
        return validateAggregate("categories", categories, this.categoryGateway::existsByIds);
    }

    private ValidateHandler validateGenres(final Set<GenreID> genres) {
        return validateAggregate("genres", genres, this.genreGateway::existsByIds);
    }

    private ValidateHandler validateCastMembers(final Set<CastMemberID> members) {
        return validateAggregate("cast members", members, this.castMemberGateway::existsByIds);
    }

    private <T extends Identifier> ValidateHandler validateAggregate(
        final String aggregate,
        final Set<T> ids,
        final Function<Iterable<T>, List<T>> existsByIds
        ) {
        final var notification = Notification.create();
        if (ids == null || ids.isEmpty()) {
            return notification;
        }

        final var retrievedIds = existsByIds.apply(ids);
        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);

            final var missingIdsMessage = missingIds.stream()
                .map(Identifier::getValue)
                .collect(Collectors.joining(", "));

            notification.append(new Error("Some %s could not be found: %s".formatted(aggregate, missingIdsMessage)));
        }

        return notification;
    }

    private <T> Set<T> toIdentifiers(final Set<String> ids, Function<String, T> mapper) {
        return ids.stream().map(mapper).collect(Collectors.toSet());
    }

    private Supplier<DomainException> invalidRating(String rating) {
        return () -> DomainException.with(new Error("Invalid rating: %s".formatted(rating)));
    }
}
