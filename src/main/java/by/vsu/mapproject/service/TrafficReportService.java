package by.vsu.mapproject.service;

import by.vsu.mapproject.entity.DailyTrafficSummary;
import by.vsu.mapproject.repository.TrafficDataRepository;
import by.vsu.mapproject.dto.TrafficReportResponse;
import by.vsu.mapproject.entity.Intersection;
import by.vsu.mapproject.repository.IntersectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TrafficReportService {

    private final TrafficDataRepository trafficDataRepository;
    private final IntersectionRepository intersectionRepository;
    private final GoogleDocsService googleDocsService;

    private static final List<TimePeriod> TIME_PERIODS = Arrays.asList(
            new TimePeriod(1, LocalTime.of(6, 0), LocalTime.of(9, 0), "06:00-09:00"),
            new TimePeriod(2, LocalTime.of(9, 0), LocalTime.of(12, 0), "09:00-12:00"),
            new TimePeriod(3, LocalTime.of(12, 0), LocalTime.of(15, 0), "12:00-15:00"),
            new TimePeriod(4, LocalTime.of(15, 0), LocalTime.of(18, 0), "15:00-18:00"),
            new TimePeriod(5, LocalTime.of(18, 0), LocalTime.of(21, 0), "18:00-21:00")
    );

    public TrafficReportResponse generateReport(Long intersectionId, LocalDate startDate, LocalDate endDate) {
        Intersection intersection = intersectionRepository.findById(intersectionId)
                .orElseThrow(() -> new RuntimeException("Intersection not found"));

        List<DailyTrafficSummary> dailyData = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DailyTrafficSummary dailySummary = calculateDailySummary(currentDate, intersectionId);
            if (hasData(dailySummary)) {
                dailyData.add(dailySummary);
            }
            currentDate = currentDate.plusDays(1);
        }

        TrafficReportResponse response = new TrafficReportResponse();
        response.setIntersectionName(intersection.getName());
        response.setPeriod(startDate.format(DateTimeFormatter.ISO_DATE) + " - " +
                endDate.format(DateTimeFormatter.ISO_DATE));
        response.setDailyData(dailyData);

        return response;
    }

    private DailyTrafficSummary calculateDailySummary(LocalDate date, Long intersectionId) {
        DailyTrafficSummary summary = new DailyTrafficSummary();
        summary.setDate(date);

        for (int i = 0; i < TIME_PERIODS.size(); i++) {
            TimePeriod period = TIME_PERIODS.get(i);

            List<Object[]> results = trafficDataRepository.findMaxDirectionForPeriod(
                    date,
                    intersectionId,
                    period.startTime,
                    period.endTime
            );

            if (!results.isEmpty()) {
                Object[] topResult = results.get(0); // Берём направление с максимумом
                String direction = (String) topResult[0];
                Long totalCount = (Long) topResult[1];

                setPeriodData(summary, i + 1, direction, totalCount.intValue());
            } else {
                setPeriodData(summary, i + 1, "-", 0);
            }
        }

        return summary;
    }

    private void setPeriodData(DailyTrafficSummary summary, int period, String direction, Integer count) {
        switch (period) {
            case 1:
                summary.setPeriod1MaxDirection(direction);
                summary.setPeriod1MaxCount(count);
                break;
            case 2:
                summary.setPeriod2MaxDirection(direction);
                summary.setPeriod2MaxCount(count);
                break;
            case 3:
                summary.setPeriod3MaxDirection(direction);
                summary.setPeriod3MaxCount(count);
                break;
            case 4:
                summary.setPeriod4MaxDirection(direction);
                summary.setPeriod4MaxCount(count);
                break;
            case 5:
                summary.setPeriod5MaxDirection(direction);
                summary.setPeriod5MaxCount(count);
                break;
        }
    }

    private boolean hasData(DailyTrafficSummary summary) {
        return summary.getPeriod1MaxCount() > 0 ||
                summary.getPeriod2MaxCount() > 0 ||
                summary.getPeriod3MaxCount() > 0 ||
                summary.getPeriod4MaxCount() > 0 ||
                summary.getPeriod5MaxCount() > 0;
    }

    public String generateGoogleDocReport(TrafficReportResponse report, String userEmail) throws Exception {
        StringBuilder content = new StringBuilder();

        content.append("Отчёт по загруженности перекрёстка\n\n");
        content.append("Перекрёсток: ").append(report.getIntersectionName()).append("\n");
        content.append("Период: ").append(report.getPeriod()).append("\n\n");

        content.append("Дата\t06:00-09:00\t\t09:00-12:00\t\t12:00-15:00\t\t15:00-18:00\t\t18:00-21:00\n");
        content.append("\tНаправление\tК-во\tНаправление\tК-во\tНаправление\tК-во\tНаправление\tК-во\tНаправление\tК-во\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (DailyTrafficSummary day : report.getDailyData()) {
            content.append(day.getDate().format(dateFormatter)).append("\t")
                    .append(day.getPeriod1MaxDirection()).append("\t")
                    .append(day.getPeriod1MaxCount()).append("\t")
                    .append(day.getPeriod2MaxDirection()).append("\t")
                    .append(day.getPeriod2MaxCount()).append("\t")
                    .append(day.getPeriod3MaxDirection()).append("\t")
                    .append(day.getPeriod3MaxCount()).append("\t")
                    .append(day.getPeriod4MaxDirection()).append("\t")
                    .append(day.getPeriod4MaxCount()).append("\t")
                    .append(day.getPeriod5MaxDirection()).append("\t")
                    .append(day.getPeriod5MaxCount()).append("\n");
        }

        content.append("\n\nПримечание: В столбце 'Направление' указано условное обозначение направления,\n");
        content.append("для которого суммарное количество ожидающих автомобилей за период максимально.\n");
        content.append("В столбце 'К-во' — значение этого максимума.\n");

        return googleDocsService.createReport(
                "Отчёт по перекрёстку " + report.getIntersectionName(),
                content.toString(),
                userEmail
        );
    }

    @lombok.Value
    private static class TimePeriod {
        int order;
        LocalTime startTime;
        LocalTime endTime;
        String label;
    }
}