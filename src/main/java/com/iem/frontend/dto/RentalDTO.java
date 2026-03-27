package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RentalDTO {
    private Integer rentalId;
    private LocalDateTime rentalDate;
    private Long inventoryId;
    private Integer customerId;
    private LocalDateTime returnDate;
    private Integer staffId;
    private LocalDateTime lastUpdate;
}
