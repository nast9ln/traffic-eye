package by.vsu.mapproject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(columnDefinition = "TEXT")
    private String polygon; // JSON координат полигона

    @Column(columnDefinition = "TEXT")
    private String center; // центр зоны для маркера

    private String color;
    private Double area; // площадь зоны

    @Column(columnDefinition = "TEXT")
    private String boundaries; // границы зоны
}