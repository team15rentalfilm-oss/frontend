package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressDTO {
    private Integer addressId;
    private String address;
    private String address2;
    private String district;
    private Integer cityId;
    private String postalCode;
    private String phone;
    private Object location;
    private LocalDateTime lastUpdate;
}
