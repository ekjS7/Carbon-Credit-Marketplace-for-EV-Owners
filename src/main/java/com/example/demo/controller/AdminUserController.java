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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final RoleRepository roleRepository;

    // Lấy tất cả user
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Admin - Get all users");
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("Admin - Get user by id: {}", id);
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with ID: " + id));
    }

    // Cập nhật user 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest req) {
        log.info("Admin - Update user id: {}", id);

        return userRepository.findById(id)
                .<ResponseEntity<?>>map(user -> {
                    try {
                        // Cập nhật full name
                        if (req.getFullName() != null && !req.getFullName().isBlank()) {
                            user.setFullName(req.getFullName());
                        }

                        // Cập nhật role
                        if (req.getRole() != null && !req.getRole().isBlank()) {
                            String newRole = req.getRole().toUpperCase();
                            List<String> allowedRoles = List.of("EV_OWNER", "CC_BUYER", "CVA", "ADMIN");
                            if (!allowedRoles.contains(newRole)) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body("Invalid role: " + newRole);
                            }

                            user.getRoles().clear();
                            Optional<Role> existingRole = roleRepository.findByName(newRole);
                            Role role = existingRole.orElseGet(() -> {
                                Role newR = new Role();
                                newR.setName(newRole);
                                return roleRepository.save(newR);
                            });
                            user.getRoles().add(role);
                        }

                        userRepository.save(user);
                        log.info("Updated user {} successfully", id);
                        return ResponseEntity.ok(user);
                    } catch (Exception e) {
                        log.error("Error updating user {}: {}", id, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error updating user: " + e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with ID: " + id));
    }

    // Approve user
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
    log.info("Admin - Approve user id: {}", id);

    try {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }

        UserStatusRecord record = userStatusRepository.findByUserId(id)
                .orElse(new UserStatusRecord());
        record.setUserId(id);
        record.setStatus("APPROVED");

        userStatusRepository.save(record);
        log.info("User {} approved successfully", id);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "User approved successfully",
                        "userId", id,
                        "status", "APPROVED"
                )
        );
    } catch (Exception e) {
        log.error("Error approving user {}: {}", id, e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of(
                        "error", "Internal error while approving user",
                        "details", e.getMessage()
                ));
    }
}
    // Ban user
    @PostMapping("/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        log.info("Admin - Ban user id: {}", id);

        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }

        UserStatusRecord record = userStatusRepository.findByUserId(id)
                .orElse(new UserStatusRecord());
        record.setUserId(id);
        record.setStatus("BANNED");
        userStatusRepository.save(record);

        log.info("User {} has been banned", id);
        return ResponseEntity.ok("User " + id + " has been banned");
    }

    //Unban user
    @PostMapping("/{id}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable Long id) {
        log.info("Admin - Unban user id: {}", id);

        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }

        UserStatusRecord record = userStatusRepository.findByUserId(id)
                .orElse(new UserStatusRecord());
        record.setUserId(id);
        record.setStatus("ACTIVE");
        userStatusRepository.save(record);

        log.info("User {} has been reactivated", id);
        return ResponseEntity.ok("User " + id + " has been reactivated");
    }

    //Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Admin - Delete user id: {}", id);
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok("Deleted user with ID: " + id);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with ID: " + id));
    }
}
