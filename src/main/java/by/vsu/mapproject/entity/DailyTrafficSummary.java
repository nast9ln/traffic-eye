package by.vsu.mapproject.entity;


import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyTrafficSummary {
    private LocalDate date;

    private String period1MaxDirection;
    private Integer period1MaxCount;

    private String period2MaxDirection;
    private Integer period2MaxCount;

    private String period3MaxDirection;
    private Integer period3MaxCount;

    private String period4MaxDirection;
    private Integer period4MaxCount;

    private String period5MaxDirection;
    private Integer period5MaxCount;
}