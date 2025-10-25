package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record VideosListResponse(
    @JsonProperty("id") String id,
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("updated_at") Instant updatedAt
) {

}
