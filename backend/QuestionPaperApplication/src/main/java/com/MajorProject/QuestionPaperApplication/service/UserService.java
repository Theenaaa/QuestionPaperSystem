package com.MajorProject.QuestionPaperApplication.service;

import com.MajorProject.QuestionPaperApplication.model.User;
import com.MajorProject.QuestionPaperApplication.repository.UserRepository;
import com.MajorProject.QuestionPaperApplication.config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder; // 🔥 IMPORTANT

    // ✅ REGISTER (HASH PASSWORD)
    public User register(User user) {

        Optional<User> existingUser = repo.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        // Default role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // 🔐 HASH PASSWORD BEFORE SAVE
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repo.save(user);
    }

    // ❌ REMOVE validateUser (not needed anymore)
    // (or keep only if updated to use passwordEncoder.matches)

    // ✅ LOGIN / VERIFY (HASH CHECK)
    public String verify(User user) {

        Optional<User> existing = repo.findByUsername(user.getUsername());

        if (existing.isPresent() &&
                passwordEncoder.matches(user.getPassword(), existing.get().getPassword())) {

            return jwtUtil.generateToken(
                    existing.get().getUsername(),
                    existing.get().getRole()
            );
        }

        throw new RuntimeException("Invalid credentials");
    }

    // OPTIONAL
    public String getUserRole(String username) {
        return repo.findByUsername(username)
                .map(User::getRole)
                .orElse("USER");
    }
}