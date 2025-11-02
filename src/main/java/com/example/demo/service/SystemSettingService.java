package com.example.demo.service;

import com.example.demo.entity.SystemSetting;
import com.example.demo.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private final SystemSettingRepository repository;

    public List<SystemSetting> getAll() {
        return repository.findAll();
    }

    public Optional<SystemSetting> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<SystemSetting> getByKey(String key) {
        return repository.findByKey(key);
    }

    public SystemSetting save(SystemSetting setting) {
        return repository.save(setting);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
