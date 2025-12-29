package com.accidentdetection.accident.detection.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendAlertRequest {
    @NotNull(message = "Accident ID is required")
    private Long accidentId;
}

