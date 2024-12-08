package com.example.locker.controller;

import com.example.locker.dto.LockerPasswordRequest;
import com.example.locker.dto.LockerReleaseRequest;
import com.example.locker.exception.CustomException;
import com.example.locker.model.Locker;
import com.example.locker.repository.LockerRepository;
import com.example.locker.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.locker.service.LockerService;

import java.util.List;

@RestController
@RequestMapping("/lockers")
public class LockerController {
    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private BookingService bookingService; // Untuk mengelola booking dan locker logic

    @Autowired
    private LockerService lockerService;

    @GetMapping
    public List<Locker> getLockers() {
        return lockerRepository.findAll();
    }

    @PostMapping("/open")
    public String openLocker(@RequestBody LockerPasswordRequest lockerPasswordRequest) {
        boolean isPasswordValid = bookingService.verifyPasswordAndUpdateUsage(lockerPasswordRequest.getBookingId(), lockerPasswordRequest.getPassword());

        if (!isPasswordValid) {
            throw new CustomException(404, "Invalid password or password already used twice");
        }

        return "Locker opened successfully!";
    }

    // API untuk membebaskan locker
    @PostMapping("/release")
    public String releaseLocker(@RequestBody LockerReleaseRequest request) {
        return lockerService.releaseLocker(request.getBookingId(), request.getPaymentAmount());
    }

    @PostMapping("/return/{bookingId}")
    public String returnLocker(@PathVariable Long bookingId) {
        bookingService.processLateReturn(bookingId);
        return "Locker returned and penalty processed if applicable.";
    }
}
