package com.example.backend.media.service.factory;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MediaPathBuilder {

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return ext.matches("^\\.[a-z0-9]+$") ? ext : "";
    }

    public String profileImagePath(String userId, String originalFilename) {
        return "profile/" + userId + "/" + UUID.randomUUID() + getExtension(originalFilename);
    }
    public String destinationImagePath(String adminId, String destinationSlug, String originalFilename) {
        return "destinations/" + destinationSlug + "/" + adminId + "/" + UUID.randomUUID() + getExtension(originalFilename);
    }

    // MediaPathBuilder.java
    public String activityImagePath(String adminId, String destinationSlug, String originalFilename) {
        return "destinations/" + destinationSlug + "/activities/" + adminId + "/" + UUID.randomUUID() + getExtension(originalFilename);
    }

    public String hotelImagePath(String adminId, String hotelSlug, String originalFilename) {
        return "hotels/" + hotelSlug + "/" + adminId + "/" + UUID.randomUUID() + getExtension(originalFilename);
    }

    public String guideImagePath(String adminId, String guideSlug, String originalFilename) {
        return "guides/" + guideSlug + "/" + adminId + "/" + UUID.randomUUID() + getExtension(originalFilename);
    }
}