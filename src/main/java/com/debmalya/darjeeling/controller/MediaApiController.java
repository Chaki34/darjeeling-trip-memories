package com.debmalya.darjeeling.controller;

import com.debmalya.darjeeling.model.MediaItem;
import com.debmalya.darjeeling.service.S3MediaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaApiController {

    private final S3MediaService mediaService;

    public MediaApiController(S3MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping
    public Map<String, List<MediaItem>> allMedia() {
        return Map.of(
                "photos", mediaService.getPhotos(),
                "videos", mediaService.getVideos()
        );
    }
}
