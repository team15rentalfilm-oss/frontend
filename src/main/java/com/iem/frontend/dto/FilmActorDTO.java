package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilmActorDTO {
    private Integer actorId;
    private Integer filmId;
    private LocalDateTime lastUpdate;
}
