package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;

import java.util.Objects;
import java.util.UUID;

public class VideoID extends Identifier {
    private final String value;

    private VideoID(final String value) {
        this.value = Objects.requireNonNull(value.toLowerCase(), "Video ID must not be null");
    }

    public static VideoID from(final String anId) {
        return new VideoID(anId);
    }

    public static VideoID unique() {
        return from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VideoID that = (VideoID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
