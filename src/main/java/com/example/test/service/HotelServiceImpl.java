package com.example.test.service;

import com.example.test.entity.Hotel;
import com.example.test.exception.MyCustomExeption;
import com.example.test.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository repository;

    public HotelServiceImpl(HotelRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Hotel> getAllDatas() {
        return repository.findAll();
    }

    @Override
    public Hotel getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new MyCustomExeption("Hotel with id " + id + " not found"));
    }

    @Override
    public Hotel saveHotel(Hotel hotel) {
        return repository.save(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        if (!repository.existsById(id)) {
            throw new MyCustomExeption("Hotel with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public Hotel updateHotel(Long id, Hotel updatedHotel) {
        Hotel existingHotel = getById(id);

        existingHotel.setName(updatedHotel.getName());
        existingHotel.setAddress(updatedHotel.getAddress());
        existingHotel.setCountry(updatedHotel.getCountry());
        existingHotel.setDescription(updatedHotel.getDescription());
        existingHotel.setRating(updatedHotel.getRating());

        return repository.save(existingHotel);
    }
}
