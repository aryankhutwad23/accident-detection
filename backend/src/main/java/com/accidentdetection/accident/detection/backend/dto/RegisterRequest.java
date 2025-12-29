package com.accidentdetection.accident.detection.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Contact number 1 is required")
    private String contactNumber1;

    @NotBlank(message = "Contact number 2 is required")
    private String contactNumber2;
}