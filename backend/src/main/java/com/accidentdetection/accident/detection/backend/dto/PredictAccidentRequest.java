package com.accidentdetection.accident.detection.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictAccidentRequest {
    @NotNull(message = "Accelerometer X is required")
    private Double accelX;

    @NotNull(message = "Accelerometer Y is required")
    private Double accelY;

    @NotNull(message = "Accelerometer Z is required")
    private Double accelZ;

    @NotNull(message = "Gyroscope X is required")
    private Double gyroX;

    @NotNull(message = "Gyroscope Y is required")
    private Double gyroY;

    @NotNull(message = "Gyroscope Z is required")
    private Double gyroZ;

    @NotNull(message = "Speed is required")
    private Double speed;
}

