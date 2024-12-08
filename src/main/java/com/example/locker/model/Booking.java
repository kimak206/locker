package com.example.locker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "locker_id", nullable = false)
    private Locker locker;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal deposit;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "fines", precision = 10, scale = 2)
    private BigDecimal fines;

    @Column(name = "is_paid", nullable = false, columnDefinition = "boolean default false")
    private Boolean isPaid;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Column(name = "password_used", nullable = false, columnDefinition = "int default 0")
    private Integer passwordUsed;

    @Column(name = "incorrect_attempts")
    private Integer incorrectAttempts = 0; // Jumlah percobaan password yang salah

    @Column(name = "is_locked")
    private Boolean isLocked = false; // Status apakah locker sudah hangus

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }
}
