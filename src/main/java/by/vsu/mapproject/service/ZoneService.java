package by.vsu.mapproject.service;

import by.vsu.mapproject.dto.ZoneRequest;
import by.vsu.mapproject.entity.Zone;
import by.vsu.mapproject.repository.ZoneRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final MapService mapService;
    private final ObjectMapper objectMapper;

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));
    }

    public Zone createZone(ZoneRequest request) {
        Zone zone = new Zone();
        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setPolygon(request.getPolygon());
        zone.setCenter(request.getCenter());
        zone.setColor(request.getColor());

        return zoneRepository.save(zone);
    }

    public Zone updateZone(Long id, ZoneRequest request) {
        Zone zone = getZoneById(id);

        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setPolygon(request.getPolygon());
        zone.setCenter(request.getCenter());
        zone.setColor(request.getColor());

        return zoneRepository.save(zone);
    }

    public void deleteZone(Long id) {
        Zone zone = getZoneById(id);
        zoneRepository.delete(zone);
    }
}