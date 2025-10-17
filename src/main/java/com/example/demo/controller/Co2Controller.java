package com.example.demo.controller;

import com.example.demo.service.Co2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/co2")
public class Co2Controller {

    @Autowired
    private Co2Service co2Service;

    @PostMapping("/calculate")
    public String calculate(@RequestParam double baseline,
                            @RequestParam double actual,
                            @RequestParam boolean certified,
                            @RequestParam String userId) {
        return co2Service.processEmission(baseline, actual, certified, userId);
    }
}
