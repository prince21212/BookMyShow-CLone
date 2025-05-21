package com.project.boookmyshow.services;

import com.project.boookmyshow.models.User;
import com.project.boookmyshow.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String email, String password) {
        log.info("Attempting login for email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("Login failed: user with email {} not found", email);
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            log.info("Login successful for userId: {}", user.getId());
            return user;
        }

        log.warn("Login failed: password mismatch for email {}", email);
        throw new RuntimeException("Invalid password");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User signUp(String email, String password) {
        log.info("Attempting signup for email: {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            log.warn("Signup failed: user with email {} already exists", email);
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setBookings(new ArrayList<>());

        User savedUser = userRepository.save(user);
        log.info("Signup successful for userId: {}", savedUser.getId());
        return savedUser;
    }
}