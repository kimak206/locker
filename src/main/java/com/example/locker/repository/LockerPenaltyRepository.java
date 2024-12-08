package com.example.locker.repository;

import com.example.locker.model.LockerPenalty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerPenaltyRepository extends JpaRepository<LockerPenalty, Long> {
    // Method untuk mencari locker penalty berdasarkan booking_id
    LockerPenalty findByBookingBookingId(Long bookingId);
}
