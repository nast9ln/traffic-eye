package by.vsu.mapproject;


import lombok.Data;

@Data
public class ZoneRequest {
    private String name;
    private String description;
    private String polygon; // JSON полигона
    private String center; // координаты центра
    private String color;
}