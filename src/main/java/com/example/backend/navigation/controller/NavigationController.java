package com.example.backend.navigation.controller;

import com.example.backend.navigation.model.RouteHistory;
import com.example.backend.navigation.dto.RouteRequest;
import com.example.backend.navigation.dto.RouteResponse;
import com.example.backend.navigation.service.NavigationService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/navigation")
@RequiredArgsConstructor
public class NavigationController {

    private final NavigationService navigationService;

    @PostMapping("/route")
    public ResponseEntity<RouteResponse> getRoute(
            @RequestBody RouteRequest request,
            Authentication auth
    ) {
        String userId = (auth != null) ? auth.getName() : null;
        return ResponseEntity.ok(navigationService.calculateRoute(request, userId));
    }

    @GetMapping("/destinations")
    public ResponseEntity<List<CompactDestination>> getDestinations() {
        return ResponseEntity.ok(navigationService.getCompactDestinations());
    }

    @GetMapping("/history")
    public ResponseEntity<List<RouteHistory>> getHistory(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(navigationService.getSearchHistory(auth.getName()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CompactDestination {
        private String id;
        private String name;
        private Double latitude;
        private Double longitude;
        private String city;
        private String country;
    }
}
