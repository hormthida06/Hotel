package com.example.test.repository;

import com.example.test.entity.Amenities;
import com.example.test.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenities, Long> {
    boolean findByName(String name);
//    Page<Hotel> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
