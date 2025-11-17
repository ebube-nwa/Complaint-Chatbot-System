package dev.ebube.complaintchatbot.controller;


import dev.ebube.complaintchatbot.dto.LoginRequest;
import dev.ebube.complaintchatbot.dto.RegisterRequest;
import dev.ebube.complaintchatbot.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        Map<String, String> response = authService.register(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );

         if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response); // 400 Bad Request
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Map<String, String> response = authService.login(
                request.getUsername(),
                request.getPassword()
        );
        
        if (response.containsKey("error")) {
            return ResponseEntity.status(401).body(response); // 401 Unauthorized
        }
        return ResponseEntity.ok(response);
    }
}
