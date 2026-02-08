package com.example.test.repository;

import com.example.test.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByBookingId(Long bookingId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE str(p.bookingId) LIKE %:keyword%")
    Page<Payment> searchByBookingId(@Param("keyword") String keyword, Pageable pageable);

    Optional<Payment> findByBookingId(Long bookingId);
}

