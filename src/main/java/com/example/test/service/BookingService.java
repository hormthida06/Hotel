package com.example.test.service;

import com.example.test.entity.Booking;
import com.example.test.entity.Room;
import com.example.test.entity.User;
import com.example.test.repository.BookingRepository;
import com.example.test.repository.RoomRepository;
import com.example.test.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Booking createPendingBooking(
            Long roomId,
            BigDecimal price,
            LocalDate checkIn,
            LocalDate checkOut,
            String firstName,
            String lastName,
            String email,
            String phone,
            String password,
            Long userId
    ) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        User user;

        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = new User();
            user.setFullName(firstName + " " + lastName);
            user.setEmail(email);
            user.setPhone(phone);

            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
                user.setStatus(User.Status.active);
            } else {
                user.setPassword(passwordEncoder.encode("guest"));
                user.setStatus(User.Status.inactive);
            }

            user.setRole(User.Role.customer);
            userRepository.save(user);
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setCheckIn(checkIn.atTime(14, 0));
        booking.setCheckOut(checkOut.atTime(12, 0));

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        booking.setTotalPrice(price.multiply(BigDecimal.valueOf(nights)));

        booking.setStatus(Booking.Status.Pending);

        return bookingRepository.save(booking);
    }
}

