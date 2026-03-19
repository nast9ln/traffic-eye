package by.vsu.mapproject;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DutyRequest {
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private Long employeeId;
    private Long zoneId;
}