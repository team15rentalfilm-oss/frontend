package com.iem.frontend.dto;

import jdk.jfr.DataAmount;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PaymentDTO {
    private Long paymentId;
    private Long customerId;
    private Long staffId;
    private Long rentalId;
    private Double amount;
    private LocalDateTime paymentDate;


}