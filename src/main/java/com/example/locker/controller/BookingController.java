package com.example.locker.controller;

import com.example.locker.dto.AllBookingResponse;
import com.example.locker.dto.BookingRequest;
import com.example.locker.dto.BookingResponse;
import com.example.locker.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public List<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping
    public List<AllBookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
