package com.accidentdetection.accident.detection.backend.controller;

import com.accidentdetection.accident.detection.backend.service.TwilioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alert")
public class AccidentAlertController {

    private final TwilioService twilioService;

    public AccidentAlertController(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @PostMapping("/send")
    public String sendEmergencyAlert(
            @RequestParam Long userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        twilioService.sendEmergencyAlert(userId, latitude, longitude);
        return "🚨 Alert sent successfully!";
    }
}
