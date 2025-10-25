package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.MediaStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "videos_video_media")
@Entity(name = "AudioVideoMedia")
public class AudioVideoMediaJpaEntity {

    @Id
    private String id;
    @Column(name = "checksum", nullable = false)
    private String checksum;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "file_path", nullable = false)
    private String filePath;
    @Column(name = "encoded_path", nullable = false)
    private String encodedPath;
    @Column(name = "media_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    public AudioVideoMediaJpaEntity() {}

    private AudioVideoMediaJpaEntity(final String id,
                                    final String checksum,
                                    final String name,
                                    final String filePath,
                                    final String encodedPath,
                                    final MediaStatus status) {
        this.id = id;
        this.checksum = checksum;
        this.name = name;
        this.filePath = filePath;
        this.encodedPath = encodedPath;
        this.status = status;
    }

    public static AudioVideoMediaJpaEntity from(final AudioVideoMedia media) {
        return new AudioVideoMediaJpaEntity(
            media.id(),
            media.checksum(),
            media.name(),
            media.rawLocation(),
            media.encodedLocation(),
            media.status()
        );
    }

    public AudioVideoMedia toDomain() {
        return AudioVideoMedia.with(
            getId(),
            getChecksum(),
            getName(),
            getFilePath(),
            getEncodedPath(),
            getStatus()
        );
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getEncodedPath() {
        return encodedPath;
    }

    public void setEncodedPath(String encodedPath) {
        this.encodedPath = encodedPath;
    }

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(MediaStatus status) {
        this.status = status;
    }
}
