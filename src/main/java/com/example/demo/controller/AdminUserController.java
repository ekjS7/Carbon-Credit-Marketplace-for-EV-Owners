package com.example.demo.controller;

import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserStatusRecord;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "No users found"));

        List<Map<String, Object>> data = users.stream().map(u -> Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "fullName", u.getFullName(),
                "roles", u.getRoles(),
                "createdAt", u.getCreatedAt()
        )).toList();

        return ResponseEntity.ok(Map.of(
                "message", "Fetched all users successfully",
                "total", data.size(),
                "data", data
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest req) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(user -> {
                    if (req.getFullName() != null) user.setFullName(req.getFullName());
                    if (req.getRole() != null) {
                        String newRole = req.getRole().toUpperCase();
                        Optional<Role> existingRole = roleRepository.findByName(newRole);
                        Role role = existingRole.orElseGet(() -> roleRepository.save(new Role(null, newRole)));
                        user.getRoles().clear();
                        user.getRoles().add(role);
                    }
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of("message", "User updated", "id", id));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found")));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        if (!userRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        UserStatusRecord record = userStatusRepository.findByUserId(id).orElse(new UserStatusRecord());
        record.setUserId(id);
        record.setStatus("APPROVED");
        userStatusRepository.save(record);
        return ResponseEntity.ok(Map.of("message", "User approved", "id", id));
    }

    @PostMapping("/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        if (!userRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        UserStatusRecord record = userStatusRepository.findByUserId(id).orElse(new UserStatusRecord());
        record.setUserId(id);
        record.setStatus("BANNED");
        userStatusRepository.save(record);
        return ResponseEntity.ok(Map.of("message", "User banned", "id", id));
    }

    @PostMapping("/{id}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable Long id) {
        if (!userRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        UserStatusRecord record = userStatusRepository.findByUserId(id).orElse(new UserStatusRecord());
        record.setUserId(id);
        record.setStatus("ACTIVE");
        userStatusRepository.save(record);
        return ResponseEntity.ok(Map.of("message", "User unbanned", "id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(Map.of("message", "User deleted", "id", id));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found")));
    }
}
