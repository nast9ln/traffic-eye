package by.vsu.mapproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "intersections")
@Getter
@Setter
public class Intersection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String description;

    @OneToMany(mappedBy = "intersection", cascade = CascadeType.ALL)
    private List<Direction> directions = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String location;
}