package by.vsu.mapproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface DutyRepository extends JpaRepository<Duty, Long> {
    List<Duty> findByEmployeeAndDateBetween(User employee, LocalDate startDate, LocalDate endDate);
    List<Duty> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT d FROM Duty d WHERE d.date BETWEEN :startDate AND :endDate AND d.status = :status")
    List<Duty> findCompletedDuties(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("status") DutyStatus status);

    @Query("SELECT d FROM Duty d WHERE d.employee.id = :employeeId AND d.date = :date")
    List<Duty> findByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
}