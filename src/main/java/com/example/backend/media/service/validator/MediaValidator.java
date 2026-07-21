package com.example.backend.media.service.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MediaValidator {

    public void validateImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String type = file.getContentType();

        if (type == null || !type.startsWith("image/")) {
            throw new RuntimeException("Only image files allowed. Got: " + type);
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 🔥 increase limit
            throw new RuntimeException("Max 5MB allowed");
        }
    }
}