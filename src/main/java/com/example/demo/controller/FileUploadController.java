package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final String UPLOAD_DIR = "/app/uploads";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Tạo thư mục lưu file nếu chưa có
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            // Lưu file vào thư mục uploads
            String filePath = UPLOAD_DIR + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error uploading file: " + e.getMessage());
        }
    }
}
