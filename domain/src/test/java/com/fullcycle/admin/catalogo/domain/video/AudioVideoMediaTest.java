package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AudioVideoMediaTest extends UnitTest {

    @Test
    public void givenAValidParameters_whenCallNewAudioVideoMedia_thenShouldReturnAudioVideoMediaInstance() {
        // given
        final var expectedChecksum = "checksum";
        final var expectedName = "name";
        final var expectedRawLocation = "url";
        final var expectedLocationEncoded = "encodedUrl";
        final var expectedStatus = MediaStatus.PENDING;

        // when
        final var aAudioVideoMedia = AudioVideoMedia.with(expectedChecksum, expectedChecksum, expectedName, expectedRawLocation, expectedLocationEncoded, expectedStatus);

        // then
        Assertions.assertNotNull(aAudioVideoMedia);
        Assertions.assertEquals(expectedChecksum, aAudioVideoMedia.checksum());
        Assertions.assertEquals(expectedName, aAudioVideoMedia.name());
        Assertions.assertEquals(expectedRawLocation, aAudioVideoMedia.rawLocation());
        Assertions.assertEquals(expectedLocationEncoded, aAudioVideoMedia.encodedLocation());
        Assertions.assertEquals(expectedStatus, aAudioVideoMedia.status());
    }

    @Test
    public void givenATwoAudioVideoMediaWithSameChecksumAndLocation_whenCallEquals_thenShouldReturnTrue() {
        // given
        final var expectedChecksum = "checksum";
        final var expectedName = "name";
        final var expectedRawLocation = "url";
        final var expectedLocationEncoded = "encodedUrl";
        final var expectedStatus = MediaStatus.PENDING;

        // when
        final var aAudioVideoMedia = AudioVideoMedia.with(expectedChecksum, expectedChecksum, expectedName, expectedRawLocation, expectedLocationEncoded, expectedStatus);
        final var anotherAudioVideoMedia = AudioVideoMedia.with(expectedChecksum, expectedChecksum, expectedName, expectedRawLocation, expectedLocationEncoded, expectedStatus);

        // then
        Assertions.assertEquals(aAudioVideoMedia, anotherAudioVideoMedia);
        Assertions.assertNotSame(aAudioVideoMedia, anotherAudioVideoMedia);
    }

    @Test
    public void givenAInvalidParameters_whenCallNewAudioVideoMedia_thenShouldReturnError() {
        Assertions.assertThrows(NullPointerException.class, () -> AudioVideoMedia.with(null, null,"name", "rawLocation", "encodedLocation", MediaStatus.PENDING));
        Assertions.assertThrows(NullPointerException.class, () -> AudioVideoMedia.with("checksum", "checksum",null, "rawLocation", "encodedLocation", MediaStatus.PENDING));
        Assertions.assertThrows(NullPointerException.class, () -> AudioVideoMedia.with("checksum", "checksum","name", null, "encodedLocation", MediaStatus.PENDING));
        Assertions.assertThrows(NullPointerException.class, () -> AudioVideoMedia.with("checksum", "checksum","name", "rawLocation", null, MediaStatus.PENDING));
        Assertions.assertThrows(NullPointerException.class, () -> AudioVideoMedia.with("checksum", "checksum","name", "rawLocation", "encodedLocation", null));
    }
}