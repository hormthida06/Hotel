package com.example.test.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.Single;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.Available;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;


    // ===== Hotel Relation =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    // ===== Amenities Many-to-Many =====
    @ManyToMany
    @JoinTable(
            name = "room_amenities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )private Set<Amenities> amenities = new HashSet<>();

    // ===== Room Images One-to-Many =====
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImages> images = new ArrayList<>();


    // ===== Getters & Setters =====
    public Long getId() { return id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public Set<Amenities> getAmenities() { return amenities; }
    public void setAmenities(Set<Amenities> amenities) { this.amenities = amenities; }

    public void addAmenity(Amenities amenity) { this.amenities.add(amenity); }
    public void removeAmenity(Amenities amenity) { this.amenities.remove(amenity); }

    public List<RoomImages> getImages() { return images; }
    public void setImages(List<RoomImages> images) { this.images = images; }

    public void addImage(RoomImages image) {
        images.add(image);
        image.setRoom(this);
    }

    public void removeImage(RoomImages image) {
        images.remove(image);
        image.setRoom(null);
    }

    // ===== Enums =====
    public enum Status {
        Available,
        Booked,
        Maintenance
    }

    public enum Type {
        Single,
        Double,
        Suite,
        Deluxe,
        Executive,
        JuniorSuite,
        Presidential
    }
}
