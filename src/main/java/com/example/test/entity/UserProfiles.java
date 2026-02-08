package com.example.test.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_profiles")
public class UserProfiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;

    @Enumerated(EnumType.STRING)
    private Gender gender;


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum Gender {
        male, female, other
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
