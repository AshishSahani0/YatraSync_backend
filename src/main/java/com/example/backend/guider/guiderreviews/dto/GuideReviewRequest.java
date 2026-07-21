package com.example.backend.guider.guiderreviews.dto;

import lombok.Data;

@Data
public class GuideReviewRequest {
    private Integer rating;
    private String comment;
}
