package by.vsu.mapproject.repository;

import by.vsu.mapproject.entity.Direction;
import by.vsu.mapproject.entity.Intersection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DirectionRepository extends JpaRepository<Direction, Long> {
    List<Direction> findByIntersection(Intersection intersection);

    @Query("SELECT d FROM Direction d WHERE d.intersection.id = :intersectionId ORDER BY d.orderIndex")
    List<Direction> findByIntersectionIdOrdered(@Param("intersectionId") Long intersectionId);
}