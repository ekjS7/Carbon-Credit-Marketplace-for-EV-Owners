package com.example.demo.service;

import com.example.demo.dto.Co2RequestDto;
import com.example.demo.dto.Co2ResponseDto;

public interface Co2Service {
    Co2ResponseDto processEmission(Co2RequestDto request);
}
