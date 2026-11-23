
package com.debmalya.darjeeling.service;

import com.debmalya.darjeeling.model.MediaItem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class S3MediaService {

    private static final Duration URL_VALIDITY =
            Duration.ofMinutes(60);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final String bucketName;
    private final String mediaPrefix;

    public S3MediaService(
            S3Client s3Client,
            S3Presigner s3Presigner,
            @Value("${aws.s3.bucket:}") String bucketName,
            @Value("${app.media.prefix:photos/}") String mediaPrefix) {

        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;

        this.bucketName = bucketName == null
                ? ""
                : bucketName.trim();

        String prefix = mediaPrefix == null
                ? "photos/"
                : mediaPrefix.trim();

        this.mediaPrefix = prefix.isEmpty()
                ? "photos/"
                : prefix.endsWith("/")
                ? prefix
                : prefix + "/";
    }

    public List<MediaItem> getPhotos() {
        return listMedia("image");
    }

    public List<MediaItem> getVideos() {
        return listMedia("video");
    }

    private List<MediaItem> listMedia(String type) {

        if (bucketName.isBlank()) {
            return List.of();
        }

        ListObjectsV2Request request =
                ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(mediaPrefix)
                        .build();

        return s3Client.listObjectsV2Paginator(request)
                .contents()
                .stream()
                .filter(object -> !object.key().endsWith("/"))
                .filter(object -> isSupported(object.key(), type))
                .sorted(
                        Comparator.comparing(
                                S3Object::lastModified,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )
                .map(object -> toMediaItem(object, type))
                .toList();
    }

    private MediaItem toMediaItem(
            S3Object object,
            String type) {

        String key = object.key();

        String fileName = key.substring(
                key.lastIndexOf('/') + 1
        );

        String url = generatePresignedUrl(key);

        return new MediaItem(
                fileName,
                key,
                url,
                type
        );
    }

    private String generatePresignedUrl(String objectKey) {

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(URL_VALIDITY)
                        .getObjectRequest(getObjectRequest)
                        .build();

        return s3Presigner
                .presignGetObject(presignRequest)
                .url()
                .toExternalForm();
    }

    private boolean isSupported(
            String key,
            String type) {

        String lower = key.toLowerCase(Locale.ROOT);

        if ("image".equals(type)) {

            return lower.endsWith(".jpg")
                    || lower.endsWith(".jpeg")
                    || lower.endsWith(".png")
                    || lower.endsWith(".webp")
                    || lower.endsWith(".gif")
                    || lower.endsWith(".bmp")
                    || lower.endsWith(".avif")
                    || lower.endsWith(".heic")
                    || lower.endsWith(".heif");
        }

        if ("video".equals(type)) {

            return lower.endsWith(".mp4")
                    || lower.endsWith(".webm")
                    || lower.endsWith(".mov")
                    || lower.endsWith(".m4v")
                    || lower.endsWith(".3gp");
        }

        return false;
    }
}
