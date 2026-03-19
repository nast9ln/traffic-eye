package by.vsu.mapproject;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final UserService userService;
    private final DutyService dutyService;
    private final ZoneService zoneService;
    private final ReportService reportService;
    private final GoogleSheetsService googleSheetsService;

    @GetMapping("/pending-users")
    public ResponseEntity<List<UserDTO>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        try {
            userService.approveUser(id);
            return ResponseEntity.ok().body("User approved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/duty")
    public ResponseEntity<?> createDuty(@RequestBody DutyRequest request,
                                        @AuthenticationPrincipal OAuth2User principal) {
        try {
            Long managerId = Long.parseLong(principal.getAttribute("sub"));
            Duty duty = dutyService.createDuty(request, managerId);
            return ResponseEntity.ok().body("Duty created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/zones")
    public ResponseEntity<?> createZone(@RequestBody ZoneRequest request) {
        try {
            Zone zone = zoneService.createZone(request);
            return ResponseEntity.ok().body("Zone created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/zones/{id}")
    public ResponseEntity<?> updateZone(@PathVariable Long id,
                                        @RequestBody ZoneRequest request) {
        try {
            Zone zone = zoneService.updateZone(id, request);
            return ResponseEntity.ok().body("Zone updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/report")
    public ResponseEntity<?> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            String email = principal.getAttribute("email");
            String reportUrl = reportService.generateReport(from, to, email);
            return ResponseEntity.ok().body(reportUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/duties")
    public ResponseEntity<List<DutyDTO>> getDuties(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(dutyService.getDutiesForPeriod(from, to));
    }
}