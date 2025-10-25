package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImageMediaTest extends UnitTest {

    @Test
    public void givenAValidParameters_whenCallNewImageMedia_thenShouldReturnImageMediaInstance() {
        // given
        final var expectedChecksum = "checksum";
        final var expectedName = "name";
        final var expectedUrl = "url";

        // when
        final var aImageMedia = ImageMedia.with(expectedChecksum, expectedName, expectedUrl);

        // then
        Assertions.assertNotNull(aImageMedia);
        Assertions.assertEquals(expectedChecksum, aImageMedia.checksum());
        Assertions.assertEquals(expectedName, aImageMedia.name());
        Assertions.assertEquals(expectedUrl, aImageMedia.location());
    }

    @Test
    public void givenATwoImageMediaWithSameChecksumAndLocation_whenCallEquals_thenShouldReturnTrue() {
        // given
        final var expectedChecksum = "checksum";
        final var expectedUrl = "url";

        // when
        final var aImageMedia = ImageMedia.with(expectedChecksum, "RandomName", expectedUrl);
        final var anotherImageMedia = ImageMedia.with(expectedChecksum, "SimpleName", expectedUrl);

        // then
        Assertions.assertEquals(aImageMedia, anotherImageMedia);
        Assertions.assertNotSame(aImageMedia, anotherImageMedia);
    }

    @Test
    public void givenAInvalidParameters_whenCallNewImageMedia_thenShouldReturnError() {
        Assertions.assertThrows(NullPointerException.class, () -> ImageMedia.with(null, "name", "url"));
        Assertions.assertThrows(NullPointerException.class, () -> ImageMedia.with("checksum", null, "url"));
        Assertions.assertThrows(NullPointerException.class, () -> ImageMedia.with("checksum", "name", null));

    }


}