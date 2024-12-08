package com.example.locker.service;

import com.example.locker.model.Booking;
import com.example.locker.model.Locker;
import com.example.locker.model.LockerPenalty;
import com.example.locker.exception.CustomException;
import com.example.locker.repository.BookingRepository;
import com.example.locker.repository.LockerPenaltyRepository;
import com.example.locker.repository.LockerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LockerService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private LockerPenaltyRepository lockerPenaltyRepository;

    private static final double PENALTY_AMOUNT = 25000.00; // Biaya pembebasan locker

    // API untuk melakukan pembebasan locker setelah pembayaran
    public String releaseLocker(Long bookingId, Double paymentAmount) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Booking not found"));

        // Cek apakah locker terkunci
        if (!booking.getLocked()) {
            throw new CustomException(404, "Locker is not locked");
        }

        // Cek apakah pembayaran mencukupi biaya pembebasan locker
        if (paymentAmount < PENALTY_AMOUNT) {
            throw new CustomException(400, "Insufficient payment amount for locker release");
        }

        // Update status locker menjadi tersedia
        Locker locker = booking.getLocker();
        locker.setStatus(Locker.Status.AVAILABLE);
        lockerRepository.save(locker);

        //Cari lockerpenalty data dengan bookingid dan tandai sudah dibayar
        LockerPenalty lockerPenalty = lockerPenaltyRepository.findByBookingBookingId(bookingId);
        lockerPenalty.setPaid(true);
        lockerPenaltyRepository.save(lockerPenalty);

        // Update status booking untuk menandakan locker sudah dibuka
        booking.setLocked(false); // Locker tidak terkunci lagi
        bookingRepository.save(booking);

        return "Locker has been released successfully!";
    }
}

