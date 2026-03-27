package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Integer customerId;
    private Integer storeId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer addressId;
    private Boolean active;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdate;
}
