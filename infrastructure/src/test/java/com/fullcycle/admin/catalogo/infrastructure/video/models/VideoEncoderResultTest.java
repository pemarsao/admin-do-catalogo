package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fullcycle.admin.catalogo.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.assertj.core.api.Assertions;

@JacksonTest
public class VideoEncoderResultTest {

    @Autowired
    private JacksonTester<VideoEncoderResult> json;

    @Test
    public void testUnmarshalSuccessResult() throws Exception {
        final var expectedId = "123";
        final var expectedOutputBucket = "output-bucket";
        final var expectedEncodedVideoFolder = "encoded-folder";
        final var expectedResourceId = "resource-123";
        final var expectedFilePath = "/path/to/video.mp4";
        final var expectedStatus = "COMPLETED";

        final var jsonContent = """
            {
                "id": "%s",
                "output_bucket_path": "%s",
                "video": {
                    "encoded_video_folder": "%s",
                    "resource_id": "%s",
                    "file_path": "%s"
                },
                "status": "%s"
            }
            """.formatted(expectedId, expectedOutputBucket, expectedEncodedVideoFolder, expectedResourceId, expectedFilePath, expectedStatus);

        final var actual = this.json.parse(jsonContent);

        Assertions.assertThat(actual)
            .hasFieldOrPropertyWithValue("id", expectedId)
            .hasFieldOrPropertyWithValue("outputBucket", expectedOutputBucket)
            .hasFieldOrPropertyWithValue("video.encodedVideoFolder", expectedEncodedVideoFolder)
            .hasFieldOrPropertyWithValue("video.resourceId", expectedResourceId)
            .hasFieldOrPropertyWithValue("video.filePath", expectedFilePath)
            .hasFieldOrPropertyWithValue("status", expectedStatus);
    }

    @Test
    public void testMarshalSuccessResult() throws Exception {
        final var expectedId = "123";
        final var expectedOutputBucket = "output-bucket";
        final var expectedEncodedVideoFolder = "encoded-folder";
        final var expectedResourceId = "resource-123";
        final var expectedFilePath = "/path/to/video.mp4";
        final var expectedStatus = "COMPLETED";

        final var videoMetadata = new VideoMetadata(
            expectedEncodedVideoFolder,
            expectedResourceId,
            expectedFilePath
        );

        final var videoEncoderCompleted = new VideoEncoderCompleted(
            expectedId,
            expectedOutputBucket,
            videoMetadata
        );

        final var actualJson = this.json.write(videoEncoderCompleted);

        Assertions.assertThat(actualJson)
            .hasJsonPathValue("$.id", expectedId)
            .hasJsonPathValue("$.output_bucket_path", expectedOutputBucket)
            .hasJsonPathValue("$.video.encoded_video_folder", expectedEncodedVideoFolder)
            .hasJsonPathValue("$.video.resource_id", expectedResourceId)
            .hasJsonPathValue("$.video.file_path", expectedFilePath)
            .hasJsonPathValue("$.status", expectedStatus);
    }

    @Test
    public void testUnmarshalErrorResult() throws Exception {
        final var expectedMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedId = "123";
        final var expectedFilePath = "/path/to/video.mp4";

        final var jsonContent = """
            {
                "status": "%s",
                "error": "%s",
                "message": {
                    "resource_id": "%s",
                    "file_path": "%s"
                }
            }
            """.formatted(expectedStatus, expectedMessage, expectedId, expectedFilePath);

        final var actual = this.json.parse(jsonContent);

        Assertions.assertThat(actual)
            .hasFieldOrPropertyWithValue("status", expectedStatus)
            .hasFieldOrPropertyWithValue("error", expectedMessage)
            .hasFieldOrPropertyWithValue("message.resourceId", expectedId)
            .hasFieldOrPropertyWithValue("message.filePath", expectedFilePath);
    }

    @Test
    public void testMarshalErrorResult() throws Exception {
        final var expectedMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedId = "123";
        final var expectedFilePath = "/path/to/video.mp4";

        final var videoMessage = new VideoMessage(expectedId, expectedFilePath);
        final var videoEncoderError = new VideoEncoderError(videoMessage, expectedMessage);
        final var actualJson = this.json.write(videoEncoderError);

        Assertions.assertThat(actualJson)
            .hasJsonPathValue("$.status", expectedStatus)
            .hasJsonPathValue("$.error", expectedMessage)
            .hasJsonPathValue("$.message.resource_id", expectedId)
            .hasJsonPathValue("$.message.file_path", expectedFilePath);

    }

}
