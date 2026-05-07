package com.MajorProject.QuestionPaperApplication.controller;

import com.MajorProject.QuestionPaperApplication.model.User;
import com.MajorProject.QuestionPaperApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "https://questionpapersystem.netlify.app")
public class AuthController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return service.register(user);
    }

    // ✅ RETURN TOKEN + ROLE
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {

        String token = service.verify(user);

        String role = service.getUserRole(user.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", role);

        return response;
    }
}