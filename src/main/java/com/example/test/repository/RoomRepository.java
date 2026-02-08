package com.example.test.repository;

import com.example.test.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // This must match the entity field name: room_number
    boolean existsByRoomNumber(String room_number);
}


