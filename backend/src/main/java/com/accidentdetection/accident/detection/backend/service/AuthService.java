package com.accidentdetection.accident.detection.backend.service;

import com.accidentdetection.accident.detection.backend.dto.AuthResponse;
import com.accidentdetection.accident.detection.backend.dto.FirstRegistration;
import com.accidentdetection.accident.detection.backend.dto.LoginRequest;
import com.accidentdetection.accident.detection.backend.entity.User;
import com.accidentdetection.accident.detection.backend.exception.ResourceNotFoundException;
import com.accidentdetection.accident.detection.backend.exception.UserAlreadyExistsException;
import com.accidentdetection.accident.detection.backend.repository.UserRepository;
import com.accidentdetection.accident.detection.backend.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /** REGISTER USER **/
    @Transactional
    public AuthResponse register(FirstRegistration request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered. Please use another.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getUserId());
        return new AuthResponse(token, user.getUserId(), "Registration successful");
    }

    /** LOGIN USER **/
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not registered. Please register first."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getUserId());
        return new AuthResponse(token, user.getUserId(), "Login successful");
    }
}