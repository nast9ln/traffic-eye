package by.vsu.mapproject.entity;
// TrafficData.java

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "traffic_data")
@Getter
@Setter
public class TrafficData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime startTime;

    @ManyToOne
    @JoinColumn(name = "direction_id")
    private Direction direction;

    private Integer waitingCarsCount;

    @ManyToOne
    @JoinColumn(name = "duty_id")
    private Duty duty;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}