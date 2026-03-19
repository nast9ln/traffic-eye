package by.vsu.mapproject;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final DutyRepository dutyRepository;
    private final GoogleSheetsService googleSheetsService;

    @GetMapping("/my-duties")
    public ResponseEntity<List<Duty>> getMyDuties(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        // Получение дежурств текущего сотрудника
        return ResponseEntity.ok().build();
    }

    @PostMapping("/submit-result/{dutyId}")
    public ResponseEntity<?> submitResult(
            @PathVariable Long dutyId,
            @RequestParam String spreadsheetId,
            @RequestParam String range,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            Duty duty = dutyRepository.findById(dutyId)
                    .orElseThrow(() -> new RuntimeException("Duty not found"));

            String email = principal.getAttribute("email");
            DutyResult result = googleSheetsService.importDutyResult(spreadsheetId, range, duty);

            duty.setResultLink("https://docs.google.com/spreadsheets/d/" + spreadsheetId);
            dutyRepository.save(duty);

            return ResponseEntity.ok().body("Result submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}