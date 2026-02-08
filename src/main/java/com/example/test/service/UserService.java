package com.example.test.service;

import com.example.test.entity.User;
import java.util.List;

public interface UserService {

    List<User> getAllDatas();

    User getById(Long id);

    User saveUser(User user);

    void deleteUser(Long id);

    User updateUser(Long id, User updatedUser);
}
