package by.vsu.mapproject.service;


import by.vsu.mapproject.dto.DutyDTO;
import by.vsu.mapproject.repository.DutyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

        String reportContent = formatDutiesToText(duties, period);

        return googleDocsService.createReport(title, reportContent, managerEmail);
    }

    private String formatDutiesToText(List<DutyDTO> duties, String period) {
        StringBuilder content = new StringBuilder();

        content.append("ОТЧЕТ ПО ДЕЖУРСТВАМ\n");
        content.append("===================\n\n");
        content.append("Период: ").append(period).append("\n");
        content.append("Дата формирования: ").append(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n\n");

        if (duties.isEmpty()) {
            content.append("За указанный период дежурств не найдено.\n");
            return content.toString();
        }

        content.append("Список дежурств:\n");
        content.append("----------------\n\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (DutyDTO duty : duties) {
            content.append("Дата: ").append(duty.getDate().format(dateFormatter)).append("\n");
            content.append("Время: ").append(duty.getStartTime().format(timeFormatter))
                    .append(" - ").append(duty.getEndTime().format(timeFormatter)).append("\n");
            content.append("Сотрудник: ").append(duty.getEmployeeName()).append("\n");
            content.append("Зона: ").append(duty.getZoneName()).append("\n");
            content.append("Статус: ").append(duty.getStatus()).append("\n");
            if (duty.getResultLink() != null) {
                content.append("Результат: ").append(duty.getResultLink()).append("\n");
            }
            content.append("---\n");
        }

        content.append("\nВсего дежурств: ").append(duties.size());

        return content.toString();
    }
}