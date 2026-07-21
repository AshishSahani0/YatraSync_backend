package com.example.backend.media.controller;



import com.example.backend.media.dto.MediaUploadResponse;
import com.example.backend.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media/profile")
@RequiredArgsConstructor
public class ProfileMediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<MediaUploadResponse> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication auth
    ) {

        String userId = auth.getName();

        String url = mediaService.uploadProfileImage(userId, file);

        return ResponseEntity.ok(new MediaUploadResponse(url));
    }
}
