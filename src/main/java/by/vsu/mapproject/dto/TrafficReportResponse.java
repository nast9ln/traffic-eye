package by.vsu.mapproject.dto;

import by.vsu.mapproject.entity.DailyTrafficSummary;
import lombok.Data;
import java.util.List;

@Data
public class TrafficReportResponse {
    private String intersectionName;
    private String period;
    private List<DailyTrafficSummary> dailyData;
    private String reportUrl; // Ссылка на Google Docs отчёт
}