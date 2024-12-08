package com.example.locker.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookingRequest {
    private Long userId;
    private List<Long> lockerIds; // Maksimal 3 locker
    private LocalDate startDate;
    private LocalDate endDate; // Opsional, bisa null jika tidak tahu kapan selesai
}
