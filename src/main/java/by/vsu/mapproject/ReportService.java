package by.vsu.mapproject;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DutyRepository dutyRepository;
    private final GoogleDocsService googleDocsService;
    private final DutyService dutyService;

    public String generateReport(LocalDate from, LocalDate to, String managerEmail) throws Exception {
        List<DutyDTO> duties = dutyService.getCompletedDuties(from, to);

        String period = from.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                to.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        String title = "Отчет по дежурствам за период " + period;

        return googleDocsService.createReport(title, period, duties, managerEmail);
    }
}