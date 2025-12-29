package com.accidentdetection.accident.detection.backend.service;

import com.accidentdetection.accident.detection.backend.dto.PredictAccidentRequest;
import com.accidentdetection.accident.detection.backend.dto.PredictAccidentResponse;
import com.accidentdetection.accident.detection.backend.dto.SensorDataRequest;
import com.accidentdetection.accident.detection.backend.entity.AccidentLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SensorService {
    private final MlService mlService;
    private final AccidentService accidentService;

    public SensorService(MlService mlService, AccidentService accidentService) {
        this.mlService = mlService;
        this.accidentService = accidentService;
    }

    @Transactional
    public PredictAccidentResponse processSensorData(SensorDataRequest request, Long userId) {
        // Create prediction request from sensor data
        PredictAccidentRequest predictRequest = new PredictAccidentRequest();
        predictRequest.setAccelX(request.getAccelX());
        predictRequest.setAccelY(request.getAccelY());
        predictRequest.setAccelZ(request.getAccelZ());
        predictRequest.setGyroX(request.getGyroX());
        predictRequest.setGyroY(request.getGyroY());
        predictRequest.setGyroZ(request.getGyroZ());
        predictRequest.setSpeed(request.getSpeed());

        // Get prediction from ML service
        PredictAccidentResponse prediction = mlService.predictAccident(predictRequest);

        // If accident detected, log it and start response timer
        if (prediction.getAccident()) {
            accidentService.logAccident(
                    userId,
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now()
            );
        }

        return prediction;
    }
}

