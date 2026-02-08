package com.example.test.service;

import com.example.test.entity.HotelImages;
import java.util.List;

public interface HotelImageService {

    List<HotelImages> getAll();

    HotelImages getById(Long id);

    HotelImages save(HotelImages hotelImage);

    void delete(Long id);
}
