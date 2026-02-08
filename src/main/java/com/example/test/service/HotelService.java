package com.example.test.service;

import com.example.test.entity.Hotel;  // Use entity, not model
import java.util.List;

public interface HotelService {

    List<Hotel> getAllDatas();

    Hotel getById(Long id);

    Hotel saveHotel(Hotel hotel);

    void deleteHotel(Long id);

    Hotel updateHotel(Long id, Hotel updatedHotel);
}
