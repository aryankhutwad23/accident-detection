package com.accidentdetection.accident.detection.backend.dto;

import com.accidentdetection.accident.detection.backend.entity.ResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseRequest {
    @NotNull(message = "Accident ID is required")
    private Long accidentId;

    @NotNull(message = "Response status is required")
    private ResponseStatus response;
}

