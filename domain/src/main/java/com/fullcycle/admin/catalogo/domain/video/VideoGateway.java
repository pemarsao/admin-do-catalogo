package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;

import java.util.Optional;

public interface VideoGateway {

    Video create(Video aVideo);

    void deleteById(VideoID videoID);

    Optional<Video> findById(VideoID videoID);

    Pagination<VideoPreview> findAll(VideoSearchQuery aQuery);

    Video update(Video aVideo);

}
