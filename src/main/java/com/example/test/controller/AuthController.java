package com.example.test.controller;

import com.example.test.entity.User;
import com.example.test.entity.UserProfiles;
import com.example.test.repository.UserRepository;
import com.example.test.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ===== REGISTER =====
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFirstName() + " " + request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.customer);
        user.setStatus(User.Status.active);

        userRepository.save(user);

        return ResponseEntity.ok("Account created successfully");
    }


    // ===== LOGIN =====
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        if (user.getStatus() != User.Status.active) {
            return ResponseEntity.status(403).body("Account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail());


        session.setAttribute("loggedUser", user);

        return ResponseEntity.ok(new LoginResponse(token, user.getFullName(), user.getRole()));
    }

    // ===== LOGOUT =====
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // No server-side action needed for stateless JWT
        return ResponseEntity.ok("Logged out successfully");
    }

    // ===== Request & Response DTOs =====
    public static class RegisterRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String password;
        private String country;
        private String gender;

        // getters & setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        // getters & setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private String fullName;
        private User.Role role;

        public LoginResponse(String token, String fullName, User.Role role) {
            this.token = token;
            this.fullName = fullName;
            this.role = role;
        }

        // getters
        public String getToken() { return token; }
        public String getFullName() { return fullName; }
        public User.Role getRole() { return role; }
    }
}
