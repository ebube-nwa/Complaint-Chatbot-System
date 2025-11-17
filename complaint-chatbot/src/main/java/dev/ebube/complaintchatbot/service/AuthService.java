package dev.ebube.complaintchatbot.service;

import dev.ebube.complaintchatbot.config.JwtUtil;
import dev.ebube.complaintchatbot.entity.User;
import dev.ebube.complaintchatbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, String> register(String username, String email, String password) {
        Map<String, String> response = new HashMap<>();
        
        // Check if username exists
        if (userRepository.existsByUsername(username)) {
            response.put("error", "Username is already taken");
            return response;
        }

        // Check if email exists
        if (userRepository.existsByEmail(email)) {
            response.put("error", "Email already exists");
            return response;
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hash password
        user.setRole("USER");
        
        userRepository.save(user);
        
        response.put("message", "User registered successfully");
        response.put("username", username);
        return response;
    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            response.put("error", "Invalid username or password");
            return response;
        }

        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("error", "Invalid username or password");
            return response;
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("message", "Login successful");

        return response;
    }
}
