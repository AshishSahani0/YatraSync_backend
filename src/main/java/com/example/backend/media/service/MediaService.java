package com.example.backend.media.service;

import com.example.backend.media.dto.MediaUploadResponse;
import com.example.backend.media.service.factory.MediaPathBuilder;
import com.example.backend.media.service.uploader.R2Uploader;
import com.example.backend.media.service.validator.MediaValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class MediaService {

    private final R2Uploader uploader;
    private final MediaValidator validator;
    private final MediaPathBuilder pathBuilder;

    @Value("${r2.public-url}")
    private String publicBaseUrl;

    public MediaService(R2Uploader uploader,
                        MediaValidator validator,
                        MediaPathBuilder pathBuilder) {
        this.uploader = uploader;
        this.validator = validator;
        this.pathBuilder = pathBuilder;
    }

    public String uploadProfileImage(String userId, MultipartFile file) {

        validator.validateImage(file);

        String key = pathBuilder.profileImagePath(userId, file.getOriginalFilename());

        return uploader.upload(file, key);
    }
    public void deleteFileByUrl(String url) {

        if (url == null || !url.contains(publicBaseUrl)) {
            return;
        }


        String key = url.replace(publicBaseUrl, "");

        uploader.delete(key);
    }

    public String uploadDestinationImage(String adminId, String slug, MultipartFile file) {

        validator.validateImage(file);

        String key = pathBuilder.destinationImagePath(adminId, slug, file.getOriginalFilename());

        return uploader.upload(file, key);
    }

    public List<MediaUploadResponse> uploadDestinationImages(
            String adminId,
            String slug,
            List<MultipartFile> files
    ) {
        return files.stream()
                .map(file -> {
                    String url = uploadDestinationImage(adminId, slug, file);
                    return new MediaUploadResponse(url);
                })
                .toList();
    }

    // MediaService.java
    public String uploadActivityImage(String adminId, String slug, MultipartFile file) {
        validator.validateImage(file);

        String key = pathBuilder.activityImagePath(adminId, slug, file.getOriginalFilename());

        return uploader.upload(file, key);
    }

    public List<MediaUploadResponse> uploadActivityImages(
            String adminId,
            String slug,
            List<MultipartFile> files
    ) {
        return files.stream()
                .map(file -> {
                    String url = uploadActivityImage(adminId, slug, file);
                    return new MediaUploadResponse(url);
                })
                .toList();
    }

    public String uploadHotelImage(String adminId, String slug, MultipartFile file) {
        validator.validateImage(file);
        String key = pathBuilder.hotelImagePath(adminId, slug, file.getOriginalFilename());
        return uploader.upload(file, key);
    }

    public List<MediaUploadResponse> uploadHotelImages(
            String adminId,
            String slug,
            List<MultipartFile> files
    ) {
        return files.stream()
                .map(file -> {
                    String url = uploadHotelImage(adminId, slug, file);
                    return new MediaUploadResponse(url);
                })
                .toList();
    }

    public String uploadGuideImage(String adminId, String slug, MultipartFile file) {
        validator.validateImage(file);
        String key = pathBuilder.guideImagePath(adminId, slug, file.getOriginalFilename());
        return uploader.upload(file, key);
    }

    public List<MediaUploadResponse> uploadGuideImages(
            String adminId,
            String slug,
            List<MultipartFile> files
    ) {
        return files.stream()
                .map(file -> {
                    String url = uploadGuideImage(adminId, slug, file);
                    return new MediaUploadResponse(url);
                })
                .toList();
    }
}