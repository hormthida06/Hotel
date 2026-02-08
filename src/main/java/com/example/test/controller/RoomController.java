package com.example.test.controller;

import com.example.test.entity.Amenities;
import com.example.test.entity.Hotel;
import com.example.test.entity.Room;
import com.example.test.entity.RoomImages;
import com.example.test.repository.AmenityRepository;
import com.example.test.repository.HotelRepository;
import com.example.test.repository.RoomImagesRepository;
import com.example.test.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/room")

public class RoomController {

    private final RoomRepository roomRepository;
    private final RoomImagesRepository roomImagesRepository;
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

    public RoomController(RoomRepository roomRepository,
                          RoomImagesRepository roomImagesRepository,
                          HotelRepository hotelRepository,
                          AmenityRepository amenityRepository) {
        this.roomRepository = roomRepository;
        this.roomImagesRepository = roomImagesRepository;
        this.hotelRepository = hotelRepository;
        this.amenityRepository = amenityRepository;
    }

    // ================= CREATE ROOM FORM =================
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("hotels", hotelRepository.findAll());
        model.addAttribute("amenities", amenityRepository.findAll());
        model.addAttribute("types", Room.Type.values());
        model.addAttribute("statuses", Room.Status.values());
        return "table/rooms/create";
    }

    // ================= CREATE ROOM =================
    @PostMapping("/create")
    public String createRoom(
            @RequestParam Long hotel_id,
            @RequestParam String room_number,
            @RequestParam String room_type,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam Integer capacity,
            @RequestParam(value = "status", defaultValue = "Available") String status,
            @RequestParam(value = "amenityIds", required = false) List<Long> amenityIds,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // ===== Find Hotel =====
            Hotel hotel = hotelRepository.findById(hotel_id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));

            // ===== Create Room =====
            Room room = new Room();
            room.setHotel(hotel);
            room.setRoomNumber(room_number);
            room.setType(Room.Type.valueOf(room_type.replaceAll("\\s+", "")));
            room.setPrice(price);
            room.setCapacity(capacity);
            room.setDescription(description);
            room.setStatus(Room.Status.valueOf(status));

            // ===== Set Amenities =====
            if (amenityIds != null && !amenityIds.isEmpty()) {
                Set<Amenities> amenities = new HashSet<>(amenityRepository.findAllById(amenityIds));
                room.setAmenities(amenities);
            }

            roomRepository.save(room);

            // ===== Save Images =====
            if (images != null && images.length > 0) {
                String uploadDir = "src/main/resources/static/img/rooms";
                Files.createDirectories(Paths.get(uploadDir));

                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String fileName = UUID.randomUUID() + "_" +
                                Paths.get(image.getOriginalFilename())
                                        .getFileName().toString().replaceAll("\\s+", "_");
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        RoomImages roomImage = new RoomImages();
                        roomImage.setRoom(room);
                        roomImage.setImageUrl("/img/rooms/" + fileName);
                        roomImagesRepository.save(roomImage);
                        room.addImage(roomImage);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", "Room created successfully!");
            return "redirect:/room/list";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error creating room: " + e.getMessage());
            return "redirect:/room/create";
        }
    }

    // ================= LIST ROOMS =================
    @GetMapping("/list")
    public String listRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Room> roomPage = roomRepository.findAll(pageable);

        model.addAttribute("roomPage", roomPage);
        model.addAttribute("pageSize", size);
        return "table/rooms/view";
    }

    // ================= EDIT ROOM =================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        model.addAttribute("room", room);
        model.addAttribute("hotels", hotelRepository.findAll());
        return "table/rooms/edit";
    }

    @PostMapping("/update/{id}")
    public String updateRoom(
            @PathVariable Long id,
            @RequestParam Long hotel_id,
            @RequestParam String room_number,
            @RequestParam String room_type,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam Integer capacity,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            RedirectAttributes redirectAttributes) {

        try {
            Room room = roomRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            Hotel hotel = hotelRepository.findById(hotel_id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));

            room.setHotel(hotel);
            room.setRoomNumber(room_number);
            room.setType(Room.Type.valueOf(room_type.toUpperCase()));
            room.setPrice(price);
            room.setDescription(description);
            room.setCapacity(capacity);

            roomRepository.save(room);

            // ===== Update image if provided =====
            if (images != null && images.length > 0) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        saveRoomImage(image, room);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", "Room updated successfully!");
            return "redirect:/room/list";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid room type: " + room_type);
            return "redirect:/room/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating room: " + e.getMessage());
            return "redirect:/room/edit/" + id;
        }
    }

    // ================= DELETE ROOM =================
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!roomRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Room not found!");
            return "redirect:/room/list";
        }

        roomRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Room deleted successfully!");
        return "redirect:/room/list";
    }

    // ================= HELPER METHOD =================
    private void saveRoomImage(MultipartFile image, Room room) throws Exception {
        String uploadDir = "src/main/resources/static/img/rooms";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        RoomImages roomImage = new RoomImages();
        roomImage.setRoom(room);
        roomImage.setImageUrl("/img/rooms/" + fileName);
        roomImagesRepository.save(roomImage);
    }

}
