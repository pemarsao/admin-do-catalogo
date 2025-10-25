package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideosListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RequestMapping(value = "videos")
@Tag(name = "video", description = "Video management API")
public interface VideoAPI {

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new video with medias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Error"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> createFull(
        @RequestParam(name = "title", required = false) String title,
        @RequestParam(name = "description", required = false) String description,
        @RequestParam(name = "year_launched", required = false) Integer yearLaunched,
        @RequestParam(name = "duration", required = false) Double duration,
        @RequestParam(name = "opened", required = false) Boolean opened,
        @RequestParam(name = "published", required = false) Boolean published,
        @RequestParam(name = "rating", required = false) String rating,
        @RequestParam(name = "categories_id", required = false) Set<String> categories,
        @RequestParam(name = "cast_member_id", required = false) Set<String> members,
        @RequestParam(name = "genres_id", required = false) Set<String> genres,
        @RequestParam(name = "video_file", required = false) MultipartFile videoFile,
        @RequestParam(name = "trailer_file", required = false) MultipartFile trailerFile,
        @RequestParam(name = "banner_file", required = false) MultipartFile bannerFile,
        @RequestParam(name = "thumb_file", required = false) MultipartFile thumbFile,
        @RequestParam(name = "thumb_half_file", required = false) MultipartFile thumbHalfFile
    );

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new video without medias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Error"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> createPart(@RequestBody CreateVideoRequest createVideoRequest);

    @Operation(summary = "Return video by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Return video successfully"),
        @ApiResponse(responseCode = "404", description = "Video not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(
        value = "{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    VideoResponse getVideoById(@PathVariable("id") String anId);

    @PutMapping(
        value = "{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update video by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated successfully"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Error"),
        @ApiResponse(responseCode = "404", description = "Video not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> update(
        @PathVariable("id") String anId,
        @RequestBody UpdateVideoRequest payload);

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete video by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "deleted by id successfully"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    void deleteById(@PathVariable("id") String anId);

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List videos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List videos"),
        @ApiResponse(responseCode = "422", description = "Invalid query params"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    Pagination<VideosListResponse> listVideos(
        @RequestParam(name = "search", required = false, defaultValue = "") String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "perPage", required = false, defaultValue = "25") int perPage,
        @RequestParam(name = "sort", required = false, defaultValue = "title") String sort,
        @RequestParam(name = "dir", required = false, defaultValue = "asc") String direction,
        @RequestParam(name = "cast_members_ids", required = false, defaultValue = "") Set<String> castMembers,
        @RequestParam(name = "categories_ids", required = false, defaultValue = "") Set<String> categories,
        @RequestParam(name = "genres_ids", required = false, defaultValue = "") Set<String> genres
    );

    @GetMapping(value = "{id}/media/{type}")
    @Operation(summary = "Get video by media by it's type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Media retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Media was not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<byte[]> getMediaByType(
        @PathVariable("id") String anId,
        @PathVariable("type") String type
    );

    @PostMapping(value = "{id}/medias/{type}")
    @Operation(summary = "Upload video by media by it's type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Media created successfully"),
        @ApiResponse(responseCode = "404", description = "Media was not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> uploadMediaByType(
        @PathVariable("id") String anId,
        @PathVariable("type") String type,
        @RequestParam("media_file") MultipartFile media
    );

}
