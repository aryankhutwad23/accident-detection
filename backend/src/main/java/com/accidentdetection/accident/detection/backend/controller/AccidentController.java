package com.accidentdetection.accident.detection.backend.controller;

import com.accidentdetection.accident.detection.backend.dto.*;
import com.accidentdetection.accident.detection.backend.service.AccidentService;
import com.accidentdetection.accident.detection.backend.service.MlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accident")
@CrossOrigin(origins = "*")
public class AccidentController {

    private final MlService mlService;
    private final AccidentService accidentService;

    public AccidentController(MlService mlService, AccidentService accidentService) {
        this.mlService = mlService;
        this.accidentService = accidentService;
    }

    @PostMapping("/predictAccident")
    public ResponseEntity<ApiResponse> predictAccident(
            @Valid @RequestBody PredictAccidentRequest request) {
        PredictAccidentResponse response = mlService.predictAccident(request);
        return ResponseEntity.ok(ApiResponse.success("Prediction completed", response));
    }

    @PostMapping("/userResponse")
    public ResponseEntity<ApiResponse> handleUserResponse(
            @Valid @RequestBody UserResponseRequest request) {
        accidentService.handleUserResponse(request.getAccidentId(), request.getResponse());
        return ResponseEntity.ok(ApiResponse.success("User response recorded"));
    }

    @PostMapping("/sendAlert")
    public ResponseEntity<ApiResponse> sendAlert(
            @Valid @RequestBody SendAlertRequest request) {
        accidentService.sendAlert(request.getAccidentId());
        return ResponseEntity.ok(ApiResponse.success("Emergency alert sent"));
    }
}

