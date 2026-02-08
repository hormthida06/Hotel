package com.example.test;

import com.example.test.entity.Hotel;
import com.example.test.entity.Room;
import com.example.test.repository.HotelRepository;
import com.example.test.repository.RoomRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class Project {
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public Project(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("hotels", hotelRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());

        return "index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "register.html";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact.html";
    }

    @GetMapping("/about")
    public String about() {
        return "about.html";
    }

    @GetMapping("/room/{id}")
    public String roomDetail(@PathVariable Long id, Model model) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Hotel hotel = room.getHotel();
        model.addAttribute("hotel", hotel);
        model.addAttribute("room", room);
        return "rooms.html";
    }

    @GetMapping("/hotel/{id}")
    public String hotelDetail(@PathVariable Long id, Model model) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        List<Room> rooms = hotel.getRooms();
        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", rooms);
        return "hotel.html";
    }


    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard.html";
    }

//    @GetMapping("/a")
//    public String booking() {
//        return "table/bookings/confirm_payment.html";
//    }

}

