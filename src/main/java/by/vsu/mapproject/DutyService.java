package by.vsu.mapproject;

// DutyService.java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DutyService {

    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final GoogleCalendarService googleCalendarService;

    @Transactional
    public Duty createDuty(DutyRequest request, Long managerId) throws Exception {
        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        Duty duty = new Duty();
        duty.setDate(request.getDate());
        duty.setStartTime(request.getStart());
        duty.setEndTime(request.getEnd());
        duty.setEmployee(employee);
        duty.setZone(zone);

        duty = dutyRepository.save(duty);

        // Создание события в Google Calendar
        try {
            String eventId = googleCalendarService.createEvent(duty);
            duty.setGoogleEventId(eventId);
            duty = dutyRepository.save(duty);
        } catch (Exception e) {
            // Логируем ошибку, но не прерываем создание дежурства
            e.printStackTrace();
        }

        return duty;
    }

    public List<DutyDTO> getDutiesForPeriod(LocalDate startDate, LocalDate endDate) {
        return dutyRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DutyDTO> getCompletedDuties(LocalDate startDate, LocalDate endDate) {
        return dutyRepository.findCompletedDuties(startDate, endDate, DutyStatus.COMPLETED)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DutyDTO convertToDTO(Duty duty) {
        DutyDTO dto = new DutyDTO();
        dto.setId(duty.getId());
        dto.setDate(duty.getDate());
        dto.setStartTime(duty.getStartTime());
        dto.setEndTime(duty.getEndTime());
        dto.setEmployeeName(duty.getEmployee().getUsername());
        dto.setZoneName(duty.getZone().getName());
        dto.setStatus(duty.getStatus());
        dto.setResultLink(duty.getResultLink());
        return dto;
    }
}