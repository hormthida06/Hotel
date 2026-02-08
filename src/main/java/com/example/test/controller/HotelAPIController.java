package com.example.test.controller;

import com.example.test.entity.Hotel;
import com.example.test.entity.HotelImages;
import com.example.test.repository.HotelImagesRepository;
import com.example.test.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/hotels")
public class HotelAPIController {

    private final HotelRepository hotelRepository;
    private final HotelImagesRepository hotelImagesRepository;

    @Autowired
    public HotelAPIController(HotelRepository hotelRepository,
                              HotelImagesRepository hotelImagesRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelImagesRepository = hotelImagesRepository;
    }

    // ================= CREATE HOTEL =================
    @PostMapping
    public ResponseEntity<?> createHotel(
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String country,
            @RequestParam String description,
            @RequestParam BigDecimal rating,
            @RequestParam(value = "images", required = false) MultipartFile[] images
    ) {
        if (hotelRepository.findByName(name).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Hotel already exists"));
        }

        try {
            Hotel hotel = new Hotel();
            hotel.setName(name);
            hotel.setAddress(address);
            hotel.setCountry(country);
            hotel.setDescription(description);
            hotel.setRating(rating);
            hotelRepository.save(hotel);

            // upload images
            if (images != null) {
                String uploadDir = "src/main/resources/static/img/hotels";
                Files.createDirectories(Paths.get(uploadDir));

                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        HotelImages img = new HotelImages();
                        img.setHotel(hotel);
                        img.setImageUrl("/img/hotels/" + fileName);
                        hotelImagesRepository.save(img);
                    }
                }
            }

            return ResponseEntity.ok(hotel);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ================= LIST HOTELS =================
    @GetMapping
    public Page<Hotel> listHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size);

        if (name != null && !name.isBlank()) {
            return hotelRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        return hotelRepository.findAll(pageable);
    }

    // ================= GET HOTEL BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<?> getHotel(@PathVariable Long id) {
        return hotelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= UPDATE HOTEL =================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHotel(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String country,
            @RequestParam String description,
            @RequestParam BigDecimal rating,
            @RequestParam(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            Hotel hotel = hotelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));

            hotel.setName(name);
            hotel.setAddress(address);
            hotel.setCountry(country);
            hotel.setDescription(description);
            hotel.setRating(rating);
            hotelRepository.save(hotel);

            return ResponseEntity.ok(hotel);

        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    // ================= DELETE HOTEL =================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long id) {
        if (!hotelRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Hotel not found"));
        }

        hotelRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Hotel deleted"));
    }
}
