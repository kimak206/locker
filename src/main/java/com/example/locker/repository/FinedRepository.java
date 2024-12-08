package com.example.locker.repository;

import com.example.locker.model.Fined;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinedRepository extends JpaRepository<Fined, Long> {
}
