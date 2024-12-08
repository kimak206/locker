package com.example.locker.service;

import com.example.locker.dto.AllBookingResponse;
import com.example.locker.dto.BookingRequest;
import com.example.locker.dto.BookingResponse;
import com.example.locker.exception.CustomException;
import com.example.locker.model.*;
import com.example.locker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LockerPenaltyRepository lockerPenaltyRepository;

    @Autowired
    private FinedRepository finedRepository;

    @Autowired
    private EmailService emailService;

    private static final int MAX_ATTEMPTS = 3;
    private static final double PENALTY_AMOUNT = 25000.00; // Biaya pembebasan locker
    private static final double FINED_RATE = 5000.00; // Biaya keterlambatan harian

    public List<BookingResponse> createBooking(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(404,"User not found"));

        List<Locker> lockers = lockerRepository.findAllById(request.getLockerIds());
        if (lockers.size() > 3) {
            throw new CustomException(400,"You can only book up to 3 lockers");
        }
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (endDate != null && !startDate.isEqual(endDate)) {
            throw new CustomException(400, "Booking can only be for 1 day");
        }
        List<BookingResponse> responses = new ArrayList<>();
        for (Locker locker : lockers) {
            if (!locker.getStatus().equals(Locker.Status.AVAILABLE)) {
                throw new CustomException(400, "Locker number " + locker.getLockerId() + " is not available");
            }

            // Generate password and create booking
            String password = UUID.randomUUID().toString().substring(0, 6);
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setLocker(locker);
            booking.setStartDate(request.getStartDate());
            booking.setEndDate(request.getEndDate());
            booking.setDeposit(BigDecimal.valueOf(10000.0)); // Rp. 10.000 per hari per locker
            booking.setTotalAmount(BigDecimal.valueOf(10000.0 * (request.getEndDate() != null ?
                    request.getEndDate().toEpochDay() - request.getStartDate().toEpochDay() : 1)));
            booking.setPassword(password);
            booking.setIsPaid(true);
            booking.setPasswordUsed(0);
            booking.setIncorrectAttempts(0);
            booking.setLocked(false);

            locker.setStatus(Locker.Status.BOOKED);
            lockerRepository.save(locker);

            Booking savedBooking = bookingRepository.save(booking);

            sendMail(request, savedBooking.getPassword());
            // Create response
            BookingResponse response = new BookingResponse();
            response.setBookingId(savedBooking.getLocker().getLockerId());
            response.setLockerId(locker.getLockerId());
            response.setLockerStatus(locker.getStatus().name());
            response.setPassword(savedBooking.getPassword());
            response.setDeposit(savedBooking.getDeposit());
            response.setStartDate(savedBooking.getStartDate());
            response.setEndDate(savedBooking.getEndDate());
            response.setIsPaid(savedBooking.getIsPaid());
            responses.add(response);
        }
        return responses;
    }

    // Verifikasi password dan update penggunaan password
    public boolean verifyPasswordAndUpdateUsage(Long bookingId, String password) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Booking not found"));

        if (booking.getLocked()) {
            throw new CustomException(400, "Locker has been locked due to multiple failed attempts");
        }
        if (booking.getPasswordUsed() >= 2) {
            throw new CustomException(400, "Password already used twice");
        }

        if (!booking.getPassword().equals(password)) {
            booking.setIncorrectAttempts(booking.getIncorrectAttempts() + 1);

            // Jika percobaan salah sudah mencapai 3, hanguskan locker dan kenakan biaya pembebasan
            if (booking.getIncorrectAttempts() >= MAX_ATTEMPTS) {
                booking.setLocked(true);  // Locker hangus
                bookingRepository.save(booking);
                //add lockerpenalty data
                LockerPenalty lockerPenalty = new LockerPenalty();
                lockerPenalty.setBooking(booking);
                lockerPenalty.setLocker(booking.getLocker());
                lockerPenalty.setUser(booking.getUser());
                lockerPenalty.setPenaltyAmount(PENALTY_AMOUNT);
                lockerPenalty.setPaid(false);
                lockerPenaltyRepository.save(lockerPenalty);


                throw new CustomException(404, "Locker locked due to multiple incorrect attempts. Please pay the penalty of Rp 25.000,00.");
            }

            bookingRepository.save(booking);
            throw new CustomException(404, "Incorrect password. Attempt " + booking.getIncorrectAttempts() + " of " + MAX_ATTEMPTS);
        }

        if (booking.getPasswordUsed() < 2) {
            booking.setPasswordUsed(booking.getPasswordUsed() + 1);
            bookingRepository.save(booking);
            return true; // Password valid, update dan lanjutkan
        }

        throw new CustomException(404, "Password has already been used twice");
    }

    public void processLateReturn(Long bookingId) {
        // Ambil data booking berdasarkan ID
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Cek apakah sudah melewati end_date
        if (booking.getEndDate() != null && LocalDate.now().isAfter(booking.getEndDate())) {
            // Hitung jumlah hari keterlambatan
            long daysLate = ChronoUnit.DAYS.between(booking.getEndDate(), LocalDate.now());

            // Hitung total denda
            double finedAmount = daysLate * FINED_RATE;

            // Buat entri di tabel Penalty
            Fined fined = new Fined();
            fined.setBooking(booking);
            fined.setPenaltyAmount(finedAmount);
            fined.setReason("Late return (" + daysLate + " days)");

            // Simpan entri denda
            finedRepository.save(fined);

            // Update denda di entitas Booking
            booking.setFines(BigDecimal.valueOf(finedAmount));
            bookingRepository.save(booking);
        }
    }

    public List<AllBookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        // Convert entity to DTO
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    private AllBookingResponse convertToBookingResponse(Booking booking) {
        return AllBookingResponse.builder()
                .bookingId(booking.getBookingId())
                .userId(booking.getUser().getUserId())
                .lockerId(booking.getLocker().getLockerId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .deposit(booking.getDeposit())
                .totalAmount(booking.getTotalAmount())
                .fines(booking.getFines())
                .isPaid(booking.getPaid())
                .password(booking.getPassword())
                .passwordUsed(booking.getPasswordUsed())
                .build();
    }

    private void sendMail(BookingRequest request, String password) {
        String emailSubject = "Booking Confirmation - Locker Rental";
        String emailBody = "Dear User,\n\n" +
                "Your booking has been confirmed. Here are the details:\n" +
                "Locker IDs: " + request.getLockerIds() + "\n" +
                "Start Date: " + request.getStartDate() + "\n" +
                "End Date: " + (request.getEndDate() != null ? request.getEndDate() : "N/A") + "\n" +
                "Password: " + password + "\n\n" +
                "Thank you for using our service.\n\nBest regards,\nYour Trusted Partner";

        // Send email
        String userEmail = getUserEmail(request.getUserId());
        try{
            emailService.sendBookingConfirmationEmail(userEmail, emailSubject, emailBody);
        }catch (Exception e) {
            System.out.println("Failed to send mail!");
            e.printStackTrace();
        }

    }

    private String getUserEmail(Long userId) {
        // Retrieve user's email from UserRepository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(404, "User not found"));
        return user.getEmail();
    }
}
