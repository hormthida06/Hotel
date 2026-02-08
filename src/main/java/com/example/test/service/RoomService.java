package com.example.test.service;

import com.example.test.entity.Amenities;
import com.example.test.entity.Hotel;
import com.example.test.entity.Room;
import com.example.test.repository.AmenityRepository;
import com.example.test.repository.HotelRepository;
import com.example.test.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AmenityRepository amenitiesRepository;

    public Room createRoom(Long hotelId, String roomNumber, Room.Type type,
                           BigDecimal price, Integer capacity, Room.Status status,
                           String description, List<Long> selectedAmenityIds) {

        // Fetch hotel
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        // Create Room
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setType(type);
        room.setPrice(price);
        room.setCapacity(capacity);
        room.setStatus(status);
        room.setDescription(description);
        room.setHotel(hotel);

        // Fetch selected amenities
        List<Amenities> selectedAmenities = amenitiesRepository.findAllById(selectedAmenityIds);
        room.setAmenities(new HashSet<>(selectedAmenities));

        // Save room with amenities
        return roomRepository.save(room);
    }
}


