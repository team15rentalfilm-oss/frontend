package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LanguageDTO {
    private Integer languageId;
    private String name;
    private LocalDateTime lastUpdate;
}
