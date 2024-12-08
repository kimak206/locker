package com.example.locker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AllBookingResponse {
    private Integer bookingId;
    private Integer userId;
    private Integer lockerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal deposit;
    private BigDecimal totalAmount;
    private BigDecimal fines;
    private Boolean isPaid;
    private String password;
    private Integer passwordUsed;
}
