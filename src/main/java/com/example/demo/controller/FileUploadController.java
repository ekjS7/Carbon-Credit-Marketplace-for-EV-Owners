package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/files") 
@CrossOrigin(origins = "*") 
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads"; // dùng relative path để chạy cả local và Docker

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            File destination = new File(dir, file.getOriginalFilename());
            file.transferTo(destination);

            return ResponseEntity.ok("Uploaded: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    //Thêm API để xem danh sách file 
    @GetMapping
    public ResponseEntity<String[]> listFiles() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists() || dir.listFiles() == null) {
            return ResponseEntity.ok(new String[]{});
        }
        String[] fileNames = dir.list((d, name) -> new File(d, name).isFile());
        return ResponseEntity.ok(fileNames);
    }
}
