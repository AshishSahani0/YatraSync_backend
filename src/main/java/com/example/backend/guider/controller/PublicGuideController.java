package com.example.backend.guider.controller;

import com.example.backend.destinations.getDestination.PageResponse;
import com.example.backend.guider.dto.GuideResponse;
import com.example.backend.guider.mapper.GuideMapper;
import com.example.backend.guider.model.Guide;
import com.example.backend.guider.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/guides")
@RequiredArgsConstructor
public class PublicGuideController {

    private final GuideService guideService;

    @GetMapping
    public PageResponse<GuideResponse> getPublicGuides(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> destinationIds,
            @RequestParam(required = false) List<String> languages,
            @RequestParam(required = false) List<String> specialties,
            @RequestParam(required = false) String guideType,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean featuredGuide,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "averageRating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        PageResponse<Guide> raw = guideService.getFilteredGuides(
                search, destinationIds, languages, specialties, guideType,
                difficultyLevel, minPrice, maxPrice, featuredGuide,
                page, size, sortBy, sortDirection
        );

        List<GuideResponse> securedList = raw.getContent().stream()
                .map(GuideMapper::toResponse)
                .toList();

        PageResponse<GuideResponse> response = new PageResponse<>();
        response.setContent(securedList);
        response.setTotalPages(raw.getTotalPages());
        response.setTotalElements(raw.getTotalElements());
        response.setSize(raw.getSize());
        response.setNumber(raw.getNumber());
        response.setFirst(raw.isFirst());
        response.setLast(raw.isLast());
        response.setEmpty(raw.isEmpty());
        return response;
    }

    @GetMapping("/slug/{slug}")
    public GuideResponse getBySlug(@PathVariable String slug) {
        return guideService.getBySlugPublic(slug);
    }
}
