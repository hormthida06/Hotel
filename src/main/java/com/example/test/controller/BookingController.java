package com.example.test.controller;

import com.example.test.entity.*;
import com.example.test.repository.BookingRepository;
import com.example.test.repository.PaymentRepository;
import com.example.test.repository.RoomRepository;
import com.example.test.repository.UserRepository;
import com.example.test.service.BookingService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/booking")
public class BookingController {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookingService bookingService;
    private final PaymentRepository paymentRepository;

    public BookingController(BookingRepository bookingRepository,
                             RoomRepository roomRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             BookingService bookingService,
                             PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookingService = bookingService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/detail")
    public String bookingDetail(
            @RequestParam Long roomId,
            @RequestParam BigDecimal price,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut,

            HttpSession session,
            Model model
    ) {

        User user = (User) session.getAttribute("loggedUser");

        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("readonly", true);
        } else {
            model.addAttribute("readonly", false);
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (checkIn == null) checkIn = LocalDate.now();
        if (checkOut == null) checkOut = checkIn.plusDays(1);
        long fee = 5;

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(nights));

        Date checkInDate = Date.from(checkIn.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date checkOutDate = Date.from(checkOut.atStartOfDay(ZoneId.systemDefault()).toInstant());

        model.addAttribute("room", room);
        model.addAttribute("roomId", room.getId());

        model.addAttribute("hotel", room.getHotel());
        model.addAttribute("checkIn", checkInDate);
        model.addAttribute("checkOut", checkOutDate);
        model.addAttribute("nights", nights);
        model.addAttribute("fee", fee);
        model.addAttribute("totalPrice", totalPrice);

        return "table/bookings/detail";
    }

    @GetMapping("/info")
    public String bookingInfo(
            @RequestParam String roomId,
            @RequestParam String price,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            Model model
    ) {
        Long roomIdLong = Long.parseLong(roomId);
        BigDecimal priceDecimal = new BigDecimal(price);

        model.addAttribute("roomId", roomIdLong);
        model.addAttribute("price", priceDecimal);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            user = userRepository.findByEmail(auth.getName()).orElse(null);
        }

        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("readonly", true);
            model.addAttribute("status", User.Status.active);
        } else {
            model.addAttribute("readonly", false);
            model.addAttribute("status", User.Status.inactive);
        }

        return "table/bookings/confirm_payment";
    }

    @PostMapping("/info")
    public String bookingInfo(
            @RequestParam Long roomId,
            @RequestParam BigDecimal price,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String gender,
            HttpSession session
    ) {

        User user = (User) session.getAttribute("loggedUser");
        System.out.println("roomId=" + roomId);
        System.out.println("price=" + price);
        System.out.println("checkIn=" + checkIn);
        System.out.println("checkOut=" + checkOut);

        if (user == null) {
            user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setFullName(first_name + " " + last_name);
                newUser.setEmail(email);
                newUser.setPhone(phone);
                newUser.setPassword(passwordEncoder.encode(password));
                newUser.setRole(User.Role.customer);
                newUser.setStatus(User.Status.inactive);

                UserProfiles profile = new UserProfiles();
                profile.setUser(newUser);
                profile.setCountry(country);
                if (gender != null) profile.setGender(UserProfiles.Gender.valueOf(gender.toUpperCase()));
                newUser.setProfile(profile);

                return userRepository.save(newUser);
            });
        } else if (user.getProfile() == null) {
            UserProfiles profile = new UserProfiles();
            profile.setUser(user);
            profile.setCountry(country);
            if (gender != null) profile.setGender(UserProfiles.Gender.valueOf(gender.toUpperCase()));
            user.setProfile(profile);
            userRepository.save(user);
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found")));

        booking.setCheckIn(checkIn.atStartOfDay());
        booking.setCheckOut(checkOut.atStartOfDay());

        booking.setTotalPrice(price);
        booking.setStatus(Booking.Status.Pending);

        bookingRepository.save(booking);

        return "redirect:/booking/payment/" + booking.getId();
    }

    // ----------------------- Payment---------------------------------
    @GetMapping("/payment/{bookingId}")
    public String showPaymentPage(@PathVariable Long bookingId, Model model) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        long nights = ChronoUnit.DAYS.between(
                booking.getCheckIn(),
                booking.getCheckOut()
        );

        model.addAttribute("booking", booking);
        model.addAttribute("room", booking.getRoom());
        model.addAttribute("user", booking.getUser());
        model.addAttribute("nights", nights);

        return "table/bookings/confirm_payment";
    }

    @GetMapping("/khqr/{bookingId}")
    public String showKhqrPage(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        long nights = ChronoUnit.DAYS.between(
                booking.getCheckIn().toLocalDate(),
                booking.getCheckOut().toLocalDate()
        );

        model.addAttribute("booking", booking);
        model.addAttribute("room", booking.getRoom());
        model.addAttribute("user", booking.getUser());
        model.addAttribute("nights", nights);
        model.addAttribute("totalPrice", booking.getTotalPrice());

        return "table/bookings/khqr_pay";
    }

    @GetMapping("/pay_success/{bookingId}")
    public String showSuccessPage(@PathVariable Long bookingId, Model model) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        long nights = ChronoUnit.DAYS.between(
                booking.getCheckIn().toLocalDate(),
                booking.getCheckOut().toLocalDate()
        );

        model.addAttribute("booking", booking);
        model.addAttribute("room", booking.getRoom());
        model.addAttribute("user", booking.getUser());
        model.addAttribute("nights", nights);
        model.addAttribute("totalPrice", booking.getTotalPrice());

        return "table/bookings/booking_success";
    }

    @Transactional
    @PostMapping("/khqr/complete/{bookingId}")
    public String completeKhqrPayment(@PathVariable Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setBooking(booking);
                    p.setAmount(booking.getTotalPrice());
                    p.setPaymentMethod(Payment.PaymentType.KHQR);
                    p.setStatus(Payment.Status.Pending);
                    return p;
                });

        Room room = booking.getRoom();
        if (room == null) {
            throw new RuntimeException("Room for this booking not found");
        }

        booking.setStatus(Booking.Status.Completed);
        payment.setStatus(Payment.Status.Completed);
        room.setStatus(Room.Status.Booked);

        bookingRepository.save(booking);
        paymentRepository.save(payment);
        roomRepository.save(room);

        return "redirect:/booking/pay_success/" + bookingId;
    }

    @PostMapping("/payment/success")
    public String paymentSuccess(@RequestParam Long bookingId, Model model) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        booking.setStatus(Booking.Status.Completed);
        payment.setStatus(Payment.Status.Completed);

        bookingRepository.save(booking);
        paymentRepository.save(payment);

        model.addAttribute("booking", booking);
        return "table/bookings/booking_success";
    }

    @PostMapping("/payment/cancel")
    public String paymentCancel(@RequestParam Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Optional<Payment> paymentOpt = paymentRepository.findByBookingId(bookingId);

        booking.setStatus(Booking.Status.Cancelled);
        bookingRepository.save(booking);

        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(Payment.Status.Failed);
            paymentRepository.save(payment);
        }

        return "redirect:/";
    }


    // ================= LIST =================
    @GetMapping("/list")
    public String listBooking(
            @RequestParam(required = false) String fullName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookingPage;

        if (fullName != null && !fullName.isBlank()) {
            bookingPage = bookingRepository.findByUser_FullNameContainingIgnoreCase(fullName, pageable);
        } else {
            bookingPage = bookingRepository.findAll(pageable);
        }

        model.addAttribute("bookingPage", bookingPage);
        model.addAttribute("pageSize", size);
        model.addAttribute("searchName", fullName);

        return "table/bookings/view";
    }

    // ================= DELETE =================
    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!bookingRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Booking not found!");
            return "redirect:/booking/list";
        }

        bookingRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Booking deleted successfully!");
        return "redirect:/booking/list";
    }

}


