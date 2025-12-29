package com.accidentdetection.accident.detection.backend.service;

import com.accidentdetection.accident.detection.backend.dto.PredictAccidentRequest;
import com.accidentdetection.accident.detection.backend.dto.PredictAccidentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MlService {
    @Value("${ml.service.url:http://localhost:5000/predict}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MlService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public PredictAccidentResponse predictAccident(PredictAccidentRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("accel_x", request.getAccelX());
            requestBody.put("accel_y", request.getAccelY());
            requestBody.put("accel_z", request.getAccelZ());
            requestBody.put("gyro_x", request.getGyroX());
            requestBody.put("gyro_y", request.getGyroY());
            requestBody.put("gyro_z", request.getGyroZ());
            requestBody.put("speed", request.getSpeed());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(mlServiceUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Boolean accident = (Boolean) body.get("accident");
                return new PredictAccidentResponse(accident, accident ? "Accident detected" : "Safe");
            }

            return new PredictAccidentResponse(false, "ML service unavailable");
        } catch (Exception e) {
            // Fallback to rule-based logic if ML service is unavailable
            return fallbackPredictAccident(request);
        }
    }

    private PredictAccidentResponse fallbackPredictAccident(PredictAccidentRequest request) {
        // Rule-based synthetic model: High acceleration + sudden speed drop → accident
        double accelMagnitude = Math.sqrt(
                Math.pow(request.getAccelX(), 2) +
                Math.pow(request.getAccelY(), 2) +
                Math.pow(request.getAccelZ(), 2)
        );

        double gyroMagnitude = Math.sqrt(
                Math.pow(request.getGyroX(), 2) +
                Math.pow(request.getGyroY(), 2) +
                Math.pow(request.getGyroZ(), 2)
        );

        // Accident conditions: high acceleration (>15 m/s²) OR high gyro (>20 rad/s) with speed drop
        boolean accidentDetected = accelMagnitude > 15.0 || (gyroMagnitude > 20.0 && request.getSpeed() < 5.0);

        return new PredictAccidentResponse(accidentDetected, accidentDetected ? "Accident detected" : "Safe");
    }
}

