package com.example.locker.dto;

public class LockerPasswordRequest {
    private Long bookingId;   // Booking ID yang terkait dengan locker
    private String password;  // Password yang diberikan untuk membuka locker

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
