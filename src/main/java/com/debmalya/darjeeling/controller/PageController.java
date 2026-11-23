package com.debmalya.darjeeling.controller;

import com.debmalya.darjeeling.service.S3MediaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final S3MediaService mediaService;

    public PageController(S3MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("photos", mediaService.getPhotos().stream().limit(6).toList());
        model.addAttribute("videos", mediaService.getVideos().stream().limit(3).toList());
        return "index";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("photos", mediaService.getPhotos());
        return "gallery";
    }

    @GetMapping("/videos")
    public String videos(Model model) {
        model.addAttribute("videos", mediaService.getVideos());
        return "videos";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
