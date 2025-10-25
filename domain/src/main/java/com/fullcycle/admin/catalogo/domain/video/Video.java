package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.event.DomainEvent;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;

import java.time.Instant;
import java.time.Year;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Video extends AggregateRoot<VideoID> {

    private String title;
    private String description;
    private Year launchedAt;
    private double duration;
    private Rating rating;

    private boolean opened;
    private boolean published;

    private Instant createdAt;
    private Instant updatedAt;

    private ImageMedia banner;
    private ImageMedia thumbnail;
    private ImageMedia thumbnailHalf;

    private AudioVideoMedia trailer;
    private AudioVideoMedia video;

    private Set<CategoryID> categories;
    private Set<GenreID> genres;
    private Set<CastMemberID> castMembers;

    protected Video(
        final VideoID videoID,
        final String title,
        final String description,
        final Year launchedAt,
        final double duration,
        final Rating rating,
        final boolean opened,
        final boolean published,
        final Instant createdAt,
        final Instant updatedAt,
        final ImageMedia banner,
        final ImageMedia thumb,
        final ImageMedia thumbHalf,
        final AudioVideoMedia trailer,
        final AudioVideoMedia video,
        final Set<CategoryID> categories,
        final Set<GenreID> genres,
        final Set<CastMemberID> castMembers,
        final List<DomainEvent> domainEvents
        ) {
        super(videoID, domainEvents);
        this.title = title;
        this.description = description;
        this.launchedAt = launchedAt;
        this.duration = duration;
        this.rating = rating;
        this.opened = opened;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.banner = banner;
        this.thumbnail = thumb;
        this.thumbnailHalf = thumbHalf;
        this.trailer = trailer;
        this.video = video;
        this.categories = categories;
        this.genres = genres;
        this.castMembers = castMembers;
    }

    public static Video newVideo(
        final String title,
        final String description,
        final Year launchedAt,
        final double duration,
        final Rating rating,
        final boolean opened,
        final boolean published,
        final Set<CategoryID> categories,
        final Set<GenreID> genres,
        final Set<CastMemberID> castMembers
    ) {
        final var videoID = VideoID.unique();
        final var now = InstantUtils.now();
        return new Video(
            videoID,
            title,
            description,
            launchedAt,
            duration,
            rating,
            opened,
            published,
            now,
            now,
            null,
            null,
            null,
            null,
            null,
            categories,
            genres,
            castMembers,
            null
        );
    }

    public static Video with(
        final String id,
        final String title,
        final String description,
        final Year launchedAt,
        final double duration,
        final Rating rating,
        final boolean opened,
        final boolean published,
        final Instant createdAt,
        final Instant updatedAt,
        final ImageMedia banner,
        final ImageMedia thumb,
        final ImageMedia thumbHalf,
        final AudioVideoMedia trailer,
        final AudioVideoMedia video,
        final Set<CategoryID> categories,
        final Set<GenreID> genres,
        final Set<CastMemberID> castMembers
    ) {
        return new Video(
            VideoID.from(id),
            title,
            description,
            launchedAt,
            duration,
            rating,
            opened,
            published,
            createdAt,
            updatedAt,
            banner,
            thumb,
            thumbHalf,
            trailer,
            video,
            categories,
            genres,
            castMembers,
            null
        );
    }

    public static Video with(final Video aVideo) {
        return new Video(
            aVideo.getId(),
            aVideo.getTitle(),
            aVideo.getDescription(),
            aVideo.getLaunchedAt(),
            aVideo.getDuration(),
            aVideo.getRating(),
            aVideo.getOpened(),
            aVideo.getPublished(),
            aVideo.getCreatedAt(),
            aVideo.getUpdatedAt(),
            aVideo.getBanner().orElse(null),
            aVideo.getThumbnail().orElse(null),
            aVideo.getThumbnailHalf().orElse(null),
            aVideo.getTrailer().orElse(null),
            aVideo.getVideo().orElse(null),
            new HashSet<>(aVideo.getCategories()),
            new HashSet<>(aVideo.getGenres()),
            new HashSet<>(aVideo.getCastMembers()),
            aVideo.getDomainEvents()
        );
    }

    public Video update(
        final String title,
        final String description,
        final Year launchedAt,
        final double duration,
        final Rating rating,
        final boolean opened,
        final boolean published,
        final Set<CategoryID> categories,
        final Set<GenreID> genres,
        final Set<CastMemberID> castMembers
    ) {
        this.updatedAt = InstantUtils.now();
        setCategories(categories);
        setGenres(genres);
        setCastMembers(castMembers);
        this.title = title;
        this.description = description;
        this.launchedAt = launchedAt;
        this.duration = duration;
        this.rating = rating;
        this.opened = opened;
        this.published = published;
        this.categories = categories;
        this.genres = genres;
        this.castMembers = castMembers;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Year getLaunchedAt() {
        return launchedAt;
    }

    public double getDuration() {
        return duration;
    }

    public Rating getRating() {
        return rating;
    }

    public boolean getOpened() {
        return opened;
    }

    public boolean getPublished() {
        return published;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Optional<ImageMedia> getBanner() {
        return Optional.ofNullable(banner);
    }

    public Video updateBannerMedia(final ImageMedia banner) {
        this.banner = banner;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Optional<ImageMedia> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public Video updateThumbnailMedia(final ImageMedia thumbnail) {
        this.thumbnail = thumbnail;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Optional<ImageMedia> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public Video updateThumbnailHalfMedia(final ImageMedia thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Optional<AudioVideoMedia> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public Video updateTrailerMedia(final AudioVideoMedia trailer) {
        this.trailer = trailer;
        this.updatedAt = InstantUtils.now();
        onAudioVideoMediaUpdated(trailer);
        return this;
    }

    public Optional<AudioVideoMedia> getVideo() {
        return Optional.ofNullable(video);
    }

    public Video updateVideoMedia(final AudioVideoMedia video) {
        this.video = video;
        this.updatedAt = InstantUtils.now();
        onAudioVideoMediaUpdated(video);
        return this;
    }

    public Set<CategoryID> getCategories() {
        return categories != null ? Collections.unmodifiableSet(categories) : Collections.emptySet();
    }

    private Video setCategories(final Set<CategoryID> categories) {
        this.categories = categories != null ? new HashSet<>(categories) : Collections.emptySet();
        return this;
    }

    public Set<GenreID> getGenres() {
        return genres != null ? Collections.unmodifiableSet(genres) : Collections.emptySet();
    }

    @Override
    public void validate(final ValidateHandler handler) {
        new VideoValidator(this, handler).validate();
    }

    private Video setGenres(final Set<GenreID> genres) {
        this.genres = genres != null ? new HashSet<>(genres) : Collections.emptySet();
        return this;
    }

    public Set<CastMemberID> getCastMembers() {
        return castMembers != null ? Collections.unmodifiableSet(castMembers) : Collections.emptySet();
    }

    private Video setCastMembers(final Set<CastMemberID> castMembers) {
        this.castMembers = castMembers != null ? new HashSet<>(castMembers) : Collections.emptySet();
        return this;
    }

    public Video processing(final VideoMediaType aType) {
        if (VideoMediaType.VIDEO == aType) {
            getVideo().ifPresent(media -> updateVideoMedia(media.processing()));
        } else if (VideoMediaType.TRAILER == aType) {
            getTrailer().ifPresent(media -> updateTrailerMedia(media.processing()));
        }
        return this;
    }

    public Video completed(VideoMediaType aType, String encodedPath) {
        if (VideoMediaType.VIDEO == aType) {
            getVideo().ifPresent(media -> updateVideoMedia(media.completed(encodedPath)));
        } else if (VideoMediaType.TRAILER == aType) {
            getTrailer().ifPresent(media -> updateTrailerMedia(media.completed(encodedPath)));
        }
        return this;
    }

    private void onAudioVideoMediaUpdated(AudioVideoMedia media) {
        if (media != null && media.isPendingEncode()) {
            this.registerEvent(new VideoMediaCreated(getId().getValue(), media.rawLocation()));
        }
    }
}
