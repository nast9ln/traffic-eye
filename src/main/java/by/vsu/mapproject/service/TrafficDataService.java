package by.vsu.mapproject.service;

import by.vsu.mapproject.repository.TrafficDataRepository;
import by.vsu.mapproject.dto.TrafficDataRequest;
import by.vsu.mapproject.entity.Direction;
import by.vsu.mapproject.entity.TrafficData;
import by.vsu.mapproject.entity.User;
import by.vsu.mapproject.repository.DirectionRepository;
import by.vsu.mapproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrafficDataService {

    private final TrafficDataRepository trafficDataRepository;
    private final DirectionRepository directionRepository;
    private final UserRepository userRepository;

    public TrafficData recordTrafficData(TrafficDataRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Direction direction = directionRepository.findById(request.getDirectionId())
                .orElseThrow(() -> new RuntimeException("Direction not found"));

        TrafficData data = new TrafficData();
        data.setDate(request.getDate());
        data.setStartTime(request.getStartTime());
        data.setDirection(direction);
        data.setWaitingCarsCount(request.getWaitingCarsCount());

        return trafficDataRepository.save(data);
    }

    public List<Direction> getDirectionsForIntersection(Long intersectionId) {
        return directionRepository.findByIntersectionIdOrdered(intersectionId);
    }
}