package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        String res = authService.register(req);
        if (res.startsWith("OK")) return ResponseEntity.ok(res);
        return ResponseEntity.badRequest().body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        String res = authService.login(req.getEmail(), req.getPassword());
        if (res.startsWith("OK")) return ResponseEntity.ok(res);
        return ResponseEntity.status(401).body(res);
    }
}
