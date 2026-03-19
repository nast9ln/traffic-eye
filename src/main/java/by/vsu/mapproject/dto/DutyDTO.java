package by.vsu.mapproject.dto;

import by.vsu.mapproject.enums.DutyStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DutyDTO {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String employeeName;
    private String zoneName;
    private DutyStatus status;
    private String resultLink;
}