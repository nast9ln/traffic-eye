package by.vsu.mapproject.repository;

import by.vsu.mapproject.dto.DutyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface DutyResultRepository extends JpaRepository<DutyResult, Long> {
    @Query("SELECT dr FROM DutyResult dr WHERE dr.importedAt BETWEEN :start AND :end")
    List<DutyResult> findByImportDateBetween(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}