package com.example.favoriteservice.service;

import com.example.favoriteservice.entity.User;
import com.example.favoriteservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }
    
    public List<User> getUsersByName(String userName) {
        return userRepository.findAll().stream()
            .filter(user -> user.getUserName().toLowerCase().contains(userName.toLowerCase()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public long getUserCount() {
        return userRepository.count();
    }
}
