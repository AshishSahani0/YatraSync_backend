package com.example.backend.destinations.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {


    @NotBlank(message = "Image URL is required")
    private String url;


    private String caption;


    private String altText;


    @Builder.Default
    private Integer order = 0;


    @Builder.Default
    private Boolean isThumbnail = false;

    @Builder.Default
    private Boolean isCover = false;


    private Integer width;
    private Integer height;
}