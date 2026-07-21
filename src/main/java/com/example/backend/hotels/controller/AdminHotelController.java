package com.example.backend.hotels.controller;

import com.example.backend.hotels.dto.HotelRequest;
import com.example.backend.hotels.dto.HotelResponse;
import com.example.backend.hotels.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hotels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminHotelController {

    private final HotelService hotelService;

    /** Create a new hotel */
    @PostMapping
    public ResponseEntity<HotelResponse> create(
            @Valid @RequestBody HotelRequest request,
            Authentication auth) {
        return ResponseEntity.status(201)
                .body(hotelService.createHotel(request, auth.getName()));
    }

    /** List all hotels (paginated) */
    @GetMapping
    public Page<HotelResponse> getAll(Pageable pageable) {
        return hotelService.getAllForAdmin(pageable);
    }

    /** List hotels by destination */
    @GetMapping("/destination/{destinationId}")
    public Page<HotelResponse> getByDestination(
            @PathVariable String destinationId, Pageable pageable) {
        return hotelService.getByDestinationForAdmin(destinationId, pageable);
    }

    /** Get a single hotel by ID */
    @GetMapping("/{id}")
    public HotelResponse getById(@PathVariable String id) {
        return hotelService.getByIdForAdmin(id);
    }

    /** Update hotel (partial, null-safe) */
    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> update(
            @PathVariable String id,
            @RequestBody HotelRequest request,
            Authentication auth) {
        return ResponseEntity.ok(hotelService.updateHotel(id, request, auth.getName()));
    }

    /** Soft-delete a hotel */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok("Hotel deleted successfully");
    }
}
