package com.example.test.controller;

import com.example.test.entity.Hotel;
import com.example.test.entity.HotelImages;
import com.example.test.repository.HotelImagesRepository;
import com.example.test.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/hotel")
public class HotelController {

    private final HotelRepository hotelRepository;
    private final HotelImagesRepository hotelImagesRepository;

    @Autowired
    public HotelController(HotelRepository hotelRepository,
                           HotelImagesRepository hotelImagesRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelImagesRepository = hotelImagesRepository;
    }

    // ================= CREATE =================
    @GetMapping("/create")
    public String showCreateForm() {
        return "table/hotels/create";
    }

    @PostMapping("/create")
    public String createHotel(
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String country,
            @RequestParam String description,
            @RequestParam BigDecimal rating,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            RedirectAttributes redirectAttributes) {

        if (hotelRepository.findByName(name).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "This Hotel already exists");
            return "redirect:/hotel/create/";
        }

        try {
            Hotel hotel = new Hotel();
            hotel.setName(name);
            hotel.setAddress(address);
            hotel.setCountry(country);
            hotel.setDescription(description);
            hotel.setRating(rating);
            hotelRepository.save(hotel);

            // ===== Upload multiple images =====
            if (images != null) {
                String uploadDir = "src/main/resources/static/img/hotels";
                Files.createDirectories(Paths.get(uploadDir));

                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        HotelImages hotelImage = new HotelImages();
                        hotelImage.setHotel(hotel);
                        hotelImage.setImageUrl("/img/hotels/" + fileName);
                        hotelImagesRepository.save(hotelImage);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", "Hotel created successfully!");
            return "redirect:/hotel/list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/hotel/create";
        }
    }


    // ================= LIST =================
    @GetMapping("/list")
    public String listHotels(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotelPage;

        if (name != null && !name.isBlank()) {
            hotelPage = hotelRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            hotelPage = hotelRepository.findAll(pageable);
        }

        model.addAttribute("hotels", hotelPage.getContent()); // actual hotel list
        model.addAttribute("hotelPage", hotelPage);           // for pagination
        model.addAttribute("searchName", name);

        return "table/hotels/view";
    }


    // ================= EDIT =================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        model.addAttribute("hotel", hotel);
        return "table/hotels/edit";
    }

    @PostMapping("/update/{id}")
    public String updateHotel(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String country,
            @RequestParam String description,
            @RequestParam BigDecimal rating,
            @RequestParam(value = "images", required = false) MultipartFile[] images, // support multiple
            RedirectAttributes redirectAttributes) {

//        if (hotelRepository.findByName(name).isPresent()){
//            redirectAttributes.addFlashAttribute("error", "This Hotel already exists");
//            return "redirect:/hotel/edit/" + id;
//        }

        try {
            // ===== Find the hotel =====
            Hotel hotel = hotelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));

            // ===== Update hotel details =====
            hotel.setName(name);
            hotel.setAddress(address);
            hotel.setCountry(country);
            hotel.setDescription(description);
            hotel.setRating(rating);
            hotelRepository.save(hotel);

            // ===== Upload new images if any =====
            if (images != null && images.length > 0) {
                String uploadDir = "src/main/resources/static/img/hotels";
                Files.createDirectories(Paths.get(uploadDir));

                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        // Save image file
                        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        // Save image URL in database
                        HotelImages hotelImage = new HotelImages();
                        hotelImage.setHotel(hotel);
                        hotelImage.setImageUrl("/img/hotels/" + fileName);
                        hotelImagesRepository.save(hotelImage);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", "Hotel updated successfully!");
            return "redirect:/hotel/list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating hotel: " + e.getMessage());
            return "redirect:/hotel/edit/" + id;
        }
    }


    // ================= DELETE =================
    @GetMapping("/delete/{id}")
    public String deleteHotel(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        if (!hotelRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Hotel not found!");
            return "redirect:/hotel/list";
        }

        hotelRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Hotel deleted successfully!");
        return "redirect:/hotel/list";
    }
}
