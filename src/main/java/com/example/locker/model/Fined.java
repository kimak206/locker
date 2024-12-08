package com.example.locker.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "fined")
public class Fined {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Booking booking;

    private Double penaltyAmount;

    private String reason;
}
