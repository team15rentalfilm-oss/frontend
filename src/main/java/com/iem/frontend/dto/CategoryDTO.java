package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDTO {
    private Integer categoryId;
    private String name;
    private LocalDateTime lastUpdate;
}
