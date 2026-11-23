package com.debmalya.darjeeling.model;

public record MediaItem(
        String fileName,
        String objectKey,
        String url,
        String mediaType
) {}
