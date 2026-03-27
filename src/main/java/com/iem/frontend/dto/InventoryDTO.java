package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryDTO {
    private Long inventoryId;
    private Integer filmId;
    private Integer storeId;
    private LocalDateTime lastUpdate;
}
