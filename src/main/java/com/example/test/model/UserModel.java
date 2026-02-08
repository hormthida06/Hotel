package com.example.test.model;

import java.time.LocalDateTime;
import com.example.test.entity.User;

public class UserModel {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String password;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== Constructors =====
    public UserModel() {}

    public UserModel(Long id, String fullName, String phone, String email, String password,
                     String role, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ===== Helper: Convert Entity -> Model =====
    public static UserModel fromEntity(User entity) {
        if (entity == null) return null;
        return new UserModel(
                entity.getId(),
                entity.getFullName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole().name(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // ===== Helper: Convert Model -> Entity =====
    public User toEntity() {
        User entity = new User();
        entity.setFullName(this.fullName);
        entity.setPhone(this.phone);
        entity.setEmail(this.email);
        entity.setPassword(this.password);
        entity.setRole(User.Role.valueOf(this.role));
        entity.setStatus(User.Status.valueOf(this.status));
        // createdAt and updatedAt are automatically handled in entity
        return entity;
    }
}
