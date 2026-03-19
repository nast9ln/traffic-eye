package by.vsu.mapproject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class DutyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "duty_id")
    private Duty duty;

    private String spreadsheetId;
    private String sheetName;
    private Integer rowNumber;

    @Column(columnDefinition = "TEXT")
    private String data; // JSON с данными из формы

    private LocalDateTime submittedAt;
    private LocalDateTime importedAt;
}