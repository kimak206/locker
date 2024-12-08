package com.example.locker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingResponse {
    private Integer bookingId;
    private Integer lockerId;
    private String lockerStatus;
    private String password;
    private BigDecimal deposit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPaid;
    private String message = "Please, Check your email for the booking detail";
}
