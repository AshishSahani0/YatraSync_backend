package com.example.backend.hotels.controller;

import com.example.backend.hotels.dto.HotelResponse;
import com.example.backend.hotels.getHotel.GetHotelService;
import com.example.backend.hotels.getHotel.HotelFilterRequest;
import com.example.backend.destinations.getDestination.PageResponse;
import com.example.backend.hotels.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/hotels")
@RequiredArgsConstructor
public class PublicHotelController {

    private final HotelService hotelService;
    private final GetHotelService getHotelService;

    /** Public pageable filter search stays endpoint */
    @GetMapping
    public PageResponse<HotelResponse> getPublicHotels(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String destinationId,
            @RequestParam(required = false) String hotelType,
            @RequestParam(required = false) Integer starRating,
            @RequestParam(required = false) Boolean coupleFriendly,
            @RequestParam(required = false) Boolean breakfastIncluded,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "popularityScore") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        HotelFilterRequest request = HotelFilterRequest.builder()
                .search(search)
                .destinationId(destinationId)
                .hotelType(hotelType)
                .starRating(starRating)
                .coupleFriendly(coupleFriendly)
                .breakfastIncluded(breakfastIncluded)
                .minRating(minRating)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return getHotelService.getFilteredHotels(request);
    }



    /** Active hotels for a destination — no auth required */
    @GetMapping("/destination/{destinationId}")
    public Page<HotelResponse> getByDestination(
            @PathVariable String destinationId, Pageable pageable) {
        return hotelService.getPublicByDestination(destinationId, pageable);
    }

    /** Hotel detail by slug — no auth required */
    @GetMapping("/slug/{slug}")
    public HotelResponse getBySlug(@PathVariable String slug) {
        return hotelService.getBySlugPublic(slug);
    }
}
