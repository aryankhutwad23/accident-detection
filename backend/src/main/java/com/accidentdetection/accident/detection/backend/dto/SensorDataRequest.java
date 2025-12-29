package com.accidentdetection.accident.detection.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataRequest {
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

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;
}

