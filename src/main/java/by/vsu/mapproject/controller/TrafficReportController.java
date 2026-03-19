package by.vsu.mapproject.controller;

import by.vsu.mapproject.dto.TrafficReportRequest;
import by.vsu.mapproject.dto.TrafficReportResponse;
import by.vsu.mapproject.service.TrafficReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/manager/reports")
@RequiredArgsConstructor
public class TrafficReportController {

    private final TrafficReportService reportService;

    @GetMapping("/traffic")
    public ResponseEntity<TrafficReportResponse> getTrafficReport(
            @RequestParam Long intersectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        TrafficReportResponse report = reportService.generateReport(intersectionId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/traffic/export")
    public ResponseEntity<String> exportTrafficReport(
            @RequestBody TrafficReportRequest request,
            @AuthenticationPrincipal OAuth2User principal) throws Exception {

        TrafficReportResponse report = reportService.generateReport(
                request.getIntersectionId(),
                request.getStartDate(),
                request.getEndDate()
        );

        String email = principal.getAttribute("email");
        String docUrl = reportService.generateGoogleDocReport(report, email);

        return ResponseEntity.ok(docUrl);
    }
}