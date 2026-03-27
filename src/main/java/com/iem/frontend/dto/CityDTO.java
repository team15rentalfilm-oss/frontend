package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CityDTO {
    private Integer cityId;
    private String city;
    private Integer countryId;
    private LocalDateTime lastUpdate;
}
