package by.vsu.mapproject.entity;

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
    private String polygon;

    @Column(columnDefinition = "TEXT")
    private String center;

    private String color;
    private Double area;

    @Column(columnDefinition = "TEXT")
    private String boundaries;
}