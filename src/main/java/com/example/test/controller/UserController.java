package com.example.test.controller;

import com.example.test.entity.User;
import com.example.test.repository.UserRepository;
import com.example.test.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;

    @GetMapping
    public List<User> getAll(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        if (jwtUtil.validateToken(token)) {
            return userRepository.findAll();
        } else {
            throw new RuntimeException("Invalid Token");
        }
    }

    // ================= LIST =================
    @GetMapping("/list")
    public String listUser(
            @RequestParam(required = false) String fullName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (fullName != null && !fullName.isBlank()) {
            userPage = userRepository.findByFullNameContainingIgnoreCase(fullName, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("userPage", userPage);
        model.addAttribute("searchName", fullName);

        return "table/users/view";
    }

    // ================= DELETE =================
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes){
        if (!userRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/user/list";
        }

        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/user/list";
    }

}
