package by.vsu.mapproject.repository;

import by.vsu.mapproject.entity.Intersection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntersectionRepository extends JpaRepository<Intersection, Long> {
}