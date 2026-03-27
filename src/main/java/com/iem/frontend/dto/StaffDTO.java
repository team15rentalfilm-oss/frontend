package com.iem.frontend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StaffDTO {
    private Integer staffId;
    private String firstName;
    private String lastName;
    private Integer addressId;
    private byte[] picture;
    private String email;
    private Integer storeId;
    private Boolean active;
    private String username;
    private String password;
    private LocalDateTime lastUpdate;
}
