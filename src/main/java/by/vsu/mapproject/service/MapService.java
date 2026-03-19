package by.vsu.mapproject.service;

// MapService.java

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createPolygonFromCoordinates(List<Coordinate> coordinates) {
        try {
            ObjectNode polygon = objectMapper.createObjectNode();
            polygon.put("type", "Polygon");
            var coordinatesArray = objectMapper.createArrayNode();
            var ringArray = objectMapper.createArrayNode();

            for (Coordinate coord : coordinates) {
                var pointArray = objectMapper.createArrayNode();
                pointArray.add(coord.getLng());
                pointArray.add(coord.getLat());
                ringArray.add(pointArray);
            }

            var firstPoint = objectMapper.createArrayNode();
            firstPoint.add(coordinates.get(0).getLng());
            firstPoint.add(coordinates.get(0).getLat());
            ringArray.add(firstPoint);

            coordinatesArray.add(ringArray);
            polygon.set("coordinates", coordinatesArray);

            return objectMapper.writeValueAsString(polygon);
        } catch (Exception e) {
            throw new RuntimeException("Error creating polygon", e);
        }
    }

    public String createMarkerFromCenter(Double lat, Double lng) {
        try {
            ObjectNode marker = objectMapper.createObjectNode();
            marker.put("type", "Point");

            var coordinatesArray = objectMapper.createArrayNode();
            coordinatesArray.add(lng);
            coordinatesArray.add(lat);

            marker.set("coordinates", coordinatesArray);

            return objectMapper.writeValueAsString(marker);
        } catch (Exception e) {
            throw new RuntimeException("Error creating marker", e);
        }
    }

    public JsonNode parsePolygon(String polygonJson) throws Exception {
        return objectMapper.readTree(polygonJson);
    }

    public static class Coordinate {
        private Double lat;
        private Double lng;

        public Coordinate(Double lat, Double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public Double getLat() { return lat; }
        public Double getLng() { return lng; }
    }
}