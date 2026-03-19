package by.vsu.mapproject.dto;


import lombok.Data;

@Data
public class ZoneRequest {
    private String name;
    private String description;
    private String polygon;
    private String center;
    private String color;
}