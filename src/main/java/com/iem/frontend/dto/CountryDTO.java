package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CountryDTO {
    private Integer countryId;
    private String country;
    private LocalDateTime lastUpdate;
}
