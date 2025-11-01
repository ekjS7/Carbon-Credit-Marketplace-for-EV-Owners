package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Lấy tất cả user
    public List<User> findUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void blockUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User verifyUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();

        // Lấy role "CVA" từ DB
        Role cvaRole = roleRepository.findByName("CVA")
                .orElseThrow(() -> new RuntimeException("Role CVA not found in DB"));

        // Gán thêm role CVA cho user
        user.getRoles().add(cvaRole);

        return userRepository.save(user);
    }
}
