package com.example.backend.media.controller;

import com.example.backend.media.dto.MediaUploadResponse;
import com.example.backend.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/media/hotels")
@RequiredArgsConstructor
public class HotelMediaController {

    private final MediaService mediaService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<List<MediaUploadResponse>> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "slug", required = false) String slug,
            Authentication auth
    ) {

        if (files == null || files.isEmpty()) {
            throw new RuntimeException("No files provided");
        }

        String adminId = auth.getName();
        String finalSlug = (slug != null && !slug.isBlank()) ? slug.trim() : "temp";

        List<MediaUploadResponse> response =
                mediaService.uploadHotelImages(adminId, finalSlug, files);

        return ResponseEntity.status(201).body(response);
    }
}
