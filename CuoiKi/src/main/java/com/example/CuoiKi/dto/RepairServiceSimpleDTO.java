package com.example.CuoiKi.dto;

public class RepairServiceSimpleDTO {
    private Integer id;
    private String name;

    public RepairServiceSimpleDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
