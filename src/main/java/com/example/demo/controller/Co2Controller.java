package com.example.demo.controller;

import com.example.demo.dto.Co2RequestDto;
import com.example.demo.dto.Co2ResponseDto;
import com.example.demo.service.Co2Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/co2")
public class Co2Controller {

    private final Co2Service co2Service;

    public Co2Controller(Co2Service co2Service) {
        this.co2Service = co2Service;
    }

    @PostMapping("/calculate")
    public ResponseEntity<Co2ResponseDto> calculate(@Valid @RequestBody Co2RequestDto req) {
        Co2ResponseDto resp = co2Service.processEmission(req);
        return ResponseEntity.ok(resp);
    }
}
