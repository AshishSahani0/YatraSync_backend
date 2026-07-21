package com.example.backend.reviewWeb.dto;

import lombok.Data;

@Data
public class WebsiteReviewRequest {
    private Integer rating;
    private String comment;
}
