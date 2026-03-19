package by.vsu.mapproject.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TrafficReportRequest {
    private Long intersectionId;
    private LocalDate startDate;
    private LocalDate endDate;
}