package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import com.fullcycle.admin.catalogo.domain.video.VideoPreview;
import com.fullcycle.admin.catalogo.domain.video.VideoSearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.fullcycle.admin.catalogo.infrastructure.services.EventService;
import com.fullcycle.admin.catalogo.infrastructure.utils.SqlUtils;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.*;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final EventService eventService;
    private final VideoRepository videoRepository;

    public DefaultVideoGateway(
        @VideoCreatedQueue final EventService eventService,
        final VideoRepository videoRepository) {
        this.eventService = Objects.requireNonNull(eventService);
        this.videoRepository = Objects.requireNonNull(videoRepository);
    }

    @Override
    @Transactional
    public Video create(final Video aVideo) {
        return save(aVideo);
    }

    @Override
    public void deleteById(final VideoID videoID) {
        final var aVideoId = videoID.getValue();
        if (this.videoRepository.existsById(aVideoId)) {
            this.videoRepository.deleteById(aVideoId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Video> findById(final VideoID videoID) {
        return this.videoRepository.findById(videoID.getValue())
            .map(VideoJpaEntity::toAggregate);
    }

    @Override
    public Pagination<VideoPreview> findAll(final VideoSearchQuery aQuery) {

        final var page = PageRequest.of(
            aQuery.page(),
            aQuery.perPage(),
            Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var actualPage = this.videoRepository.findAll(
            SqlUtils.like(SqlUtils.upper(aQuery.terms())),
            nullIfEmpty(mapTo(aQuery.castMembers(),Identifier::getValue)),
            nullIfEmpty(mapTo(aQuery.categories(),Identifier::getValue)),
            nullIfEmpty(mapTo(aQuery.genres(),Identifier::getValue)),
            page
        );

        return new Pagination<>(
            actualPage.getNumber(),
            actualPage.getSize(),
            actualPage.getTotalElements(),
            actualPage.toList()
        );
    }

    @Override
    @Transactional
    public Video update(final Video aVideo) {
        return save(aVideo);
    }

    private Video save(final Video aVideo) {
        final var result = this.videoRepository.save(VideoJpaEntity.from(aVideo)).toAggregate();
        result.publishDomainEvent(this.eventService::send);
        return result;
    }
}
