package com.example.test.repository;

import com.example.test.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUser_FullNameContainingIgnoreCase(String fullName, Pageable pageable);
    boolean existsByUser_Id(Long userId);
}