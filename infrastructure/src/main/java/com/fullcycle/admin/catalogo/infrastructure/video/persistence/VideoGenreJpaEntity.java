package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import com.fullcycle.admin.catalogo.domain.genre.GenreID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "VideoGenre")
@Table(name = "videos_genres")
public class VideoGenreJpaEntity {

    @EmbeddedId
    private VideoGenreID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoJpaEntity video;

    public VideoGenreJpaEntity() {
    }

    private VideoGenreJpaEntity(VideoGenreID id, VideoJpaEntity video) {
        this.id = id;
        this.video = video;
    }

    public static VideoGenreJpaEntity from(final VideoJpaEntity video, final GenreID genre) {
        return new VideoGenreJpaEntity(VideoGenreID.from(video.getId(), genre.getValue()), video);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VideoGenreJpaEntity that = (VideoGenreJpaEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getVideo(), that.getVideo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVideo());
    }

    public VideoGenreID getId() {
        return id;
    }

    public VideoGenreJpaEntity setId(VideoGenreID id) {
        this.id = id;
        return this;
    }

    public VideoJpaEntity getVideo() {
        return video;
    }

    public VideoGenreJpaEntity setVideo(VideoJpaEntity video) {
        this.video = video;
        return this;
    }
}
