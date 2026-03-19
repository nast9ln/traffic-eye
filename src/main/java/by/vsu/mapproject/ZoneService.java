package by.vsu.mapproject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final MapService mapService;
    private final ObjectMapper objectMapper;

    public Zone createZone(ZoneRequest request) {
        Zone zone = new Zone();
        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setPolygon(request.getPolygon());
        zone.setCenter(request.getCenter());
        zone.setColor(request.getColor());

        // Вычисление площади полигона (можно добавить логику)
        try {
            JsonNode polygon = objectMapper.readTree(request.getPolygon());
            // Вычисление площади
            zone.setArea(calculateArea(polygon));
        } catch (Exception e) {
            // Игнорируем ошибку вычисления площади
        }

        return zoneRepository.save(zone);
    }

    public Zone updateZone(Long id, ZoneRequest request) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setPolygon(request.getPolygon());
        zone.setCenter(request.getCenter());
        zone.setColor(request.getColor());

        return zoneRepository.save(zone);
    }

    private Double calculateArea(JsonNode polygon) {
        // Реализация вычисления площади полигона
        // Можно использовать алгоритм шнурка (Shoelace formula)
        return 0.0;
    }
}