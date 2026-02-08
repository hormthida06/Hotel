package com.example.test.service;

import com.example.test.entity.HotelImages;
import com.example.test.exception.MyCustomExeption;
import com.example.test.repository.HotelImagesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelImageServiceImpl implements HotelImageService {

    private final HotelImagesRepository repository;

    public HotelImageServiceImpl(HotelImagesRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<HotelImages> getAll() {
        return repository.findAll();
    }

    @Override
    public HotelImages getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new MyCustomExeption("Hotel image with id " + id + " not found"));
    }

    @Override
    public HotelImages save(HotelImages hotelImage) {
        return repository.save(hotelImage);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new MyCustomExeption("Hotel image with id " + id + " not found");
        }
        repository.deleteById(id);
    }
}
