package by.vsu.mapproject.controller;

import by.vsu.mapproject.dto.TrafficDataRequest;
import by.vsu.mapproject.entity.TrafficData;
import by.vsu.mapproject.service.TrafficDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee/traffic")
@RequiredArgsConstructor
public class TrafficDataController {

    private final TrafficDataService trafficDataService;

    @PostMapping("/record")
    public ResponseEntity<?> recordTrafficData(
            @RequestBody TrafficDataRequest request,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            String email = principal.getAttribute("email");
            TrafficData data = trafficDataService.recordTrafficData(request, email);
            return ResponseEntity.ok("Данные успешно записаны");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/directions/{intersectionId}")
    public ResponseEntity<?> getDirections(@PathVariable Long intersectionId) {
        return ResponseEntity.ok(trafficDataService.getDirectionsForIntersection(intersectionId));
    }
}