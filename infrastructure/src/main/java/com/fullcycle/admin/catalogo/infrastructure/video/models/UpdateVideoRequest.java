package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record UpdateVideoRequest(
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("duration") Double duration,
    @JsonProperty("year_launched") Integer launchYear,
    @JsonProperty("opened") Boolean opened,
    @JsonProperty("published") Boolean published,
    @JsonProperty("rating") String rating,
    @JsonProperty("cast_members") Set<String> castMembersId,
    @JsonProperty("categories") Set<String> categoriesId,
    @JsonProperty("genres") Set<String> genresId
) {

}
