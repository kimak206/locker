package com.example.locker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lockerpenalty")
public class LockerPenalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lockerPenaltyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "locker_id")
    private Locker locker;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private Double penaltyAmount = 25000.00; // Biaya pembebasan locker
    private boolean isPaid = false; // Status pembayaran penalti

    // Getters and Setters
    public Long getLockerPenaltyId() {
        return lockerPenaltyId;
    }

    public void setLockerPenaltyId(Long lockerPenaltyId) {
        this.lockerPenaltyId = lockerPenaltyId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public Double getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(Double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}

