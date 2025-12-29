package com.accidentdetection.accident.detection.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictAccidentResponse {
    private Boolean accident;
    private String message;
}

