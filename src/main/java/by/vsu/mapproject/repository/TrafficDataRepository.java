package by.vsu.mapproject.repository;

import by.vsu.mapproject.entity.TrafficData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TrafficDataRepository extends JpaRepository<TrafficData, Long> {

    List<TrafficData> findByDateBetweenAndDirectionIntersectionId(
            LocalDate startDate,
            LocalDate endDate,
            Long intersectionId
    );

    @Query("SELECT td FROM TrafficData td " +
            "WHERE td.date = :date " +
            "AND td.direction.intersection.id = :intersectionId " +
            "AND td.startTime BETWEEN :startTime AND :endTime")
    List<TrafficData> findForPeriod(
            @Param("date") LocalDate date,
            @Param("intersectionId") Long intersectionId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT td.direction.code, SUM(td.waitingCarsCount) as total " +
            "FROM TrafficData td " +
            "WHERE td.date = :date " +
            "AND td.direction.intersection.id = :intersectionId " +
            "AND td.startTime BETWEEN :startTime AND :endTime " +
            "GROUP BY td.direction.code " +
            "ORDER BY total DESC")
    List<Object[]> findMaxDirectionForPeriod(
            @Param("date") LocalDate date,
            @Param("intersectionId") Long intersectionId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}