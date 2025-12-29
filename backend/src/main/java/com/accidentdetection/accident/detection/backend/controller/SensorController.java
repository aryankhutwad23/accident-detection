package com.accidentdetection.accident.detection.backend.controller;

import com.accidentdetection.accident.detection.backend.dto.ApiResponse;
import com.accidentdetection.accident.detection.backend.dto.PredictAccidentResponse;
import com.accidentdetection.accident.detection.backend.dto.SensorDataRequest;
import com.accidentdetection.accident.detection.backend.repository.UserRepository;
import com.accidentdetection.accident.detection.backend.service.SensorService;
import com.accidentdetection.accident.detection.backend.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*")
public class SensorController {

    private final SensorService sensorService;
    private final UserRepository userRepository;

    public SensorController(SensorService sensorService, UserRepository userRepository) {
        this.sensorService = sensorService;
        this.userRepository = userRepository;
    }

    @PostMapping("/sendSensorData")
    public ResponseEntity<ApiResponse> sendSensorData(
            @Valid @RequestBody SensorDataRequest request,
            Authentication authentication) {

        // Authentication.getName() will return the username from JWT
        String username = authentication.getName();

        Long userId = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getUserId();

        PredictAccidentResponse response = sensorService.processSensorData(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Sensor data processed", response));
    }
}