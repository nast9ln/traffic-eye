package by.vsu.mapproject.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TrafficDataRequest {
    private Long intersectionId;
    private Long directionId;
    private LocalDate date;
    private LocalTime startTime;
    private Integer waitingCarsCount;
    private Long dutyId;
}