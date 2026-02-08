package com.example.test.repository;

import com.example.test.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByfull_name(String username);
    Optional<User> findByEmail(String email);
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

}
