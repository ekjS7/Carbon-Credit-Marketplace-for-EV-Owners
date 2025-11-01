package com.example.demo.controller;

import com.example.demo.entity.SystemSetting;
import com.example.demo.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@Slf4j
public class SystemSettingController {

    private final SystemSettingService service;

    // Lấy tất cả settings
    @GetMapping
    public ResponseEntity<List<SystemSetting>> getAll() {
        log.info("Admin - Get all settings");
        return ResponseEntity.ok(service.getAll());
    }

    // Lấy setting theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("Admin - Get setting by id {}", id);
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Setting not found with id " + id));
    }

    // Lấy setting theo key
    @GetMapping("/key/{key}")
    public ResponseEntity<?> getByKey(@PathVariable String key) {
        log.info("Admin - Get setting by key {}", key);
        return service.getByKey(key)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Setting not found with key " + key));
    }

    // Tạo mới setting
    @PostMapping
    public ResponseEntity<SystemSetting> create(@RequestBody SystemSetting setting) {
        log.info("Admin - Create new setting with key {}", setting.getKey());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.save(setting));
    }
}
