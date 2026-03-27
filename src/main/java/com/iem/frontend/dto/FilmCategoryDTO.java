package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilmCategoryDTO {
    private Integer filmId;
    private Integer categoryId;
    private LocalDateTime lastUpdate;
}
