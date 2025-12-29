package com.accidentdetection.accident.detection.backend.service;

import com.accidentdetection.accident.detection.backend.entity.AccidentLog;
import com.accidentdetection.accident.detection.backend.entity.ResponseStatus;
import com.accidentdetection.accident.detection.backend.entity.User;
import com.accidentdetection.accident.detection.backend.exception.ResourceNotFoundException;
import com.accidentdetection.accident.detection.backend.repository.AccidentLogRepository;
import com.accidentdetection.accident.detection.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class AccidentService {
    private final AccidentLogRepository accidentLogRepository;
    private final UserRepository userRepository;
    private final TwilioService twilioService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    
    // Track active accidents waiting for user response (accidentId -> timer task)
    private final Map<Long, CompletableFuture<Void>> activeAccidents = new ConcurrentHashMap<>();

    public AccidentService(AccidentLogRepository accidentLogRepository,
                          UserRepository userRepository,
                          TwilioService twilioService) {
        this.accidentLogRepository = accidentLogRepository;
        this.userRepository = userRepository;
        this.twilioService = twilioService;
    }

    @Transactional
    public AccidentLog logAccident(Long userId, Double latitude, Double longitude, LocalDateTime timestamp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AccidentLog accidentLog = new AccidentLog();
        accidentLog.setUser(user);
        accidentLog.setLatitude(latitude);
        accidentLog.setLongitude(longitude);
        accidentLog.setTimestamp(timestamp);
        accidentLog.setResponse(ResponseStatus.NO_RESPONSE);

        accidentLog = accidentLogRepository.save(accidentLog);

        // Start 30-second timer for auto-alert
        startResponseTimer(accidentLog.getAccidentId(), userId, latitude, longitude);

        return accidentLog;
    }

    private void startResponseTimer(Long accidentId, Long userId, Double latitude, Double longitude) {
        CompletableFuture<Void> timerTask = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(30000); // 30 seconds wait for user response
                
                // Check if response was received
                AccidentLog accidentLog = accidentLogRepository.findById(accidentId)
                        .orElse(null);
                
                if (accidentLog != null && accidentLog.getResponse() == ResponseStatus.NO_RESPONSE) {
                    // No response received within 30 seconds, send alert automatically
                    accidentLog.setResponse(ResponseStatus.NO_RESPONSE); // Keep as NO_RESPONSE
                    accidentLogRepository.save(accidentLog);
                    twilioService.sendEmergencyAlert(userId, latitude, longitude);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, scheduler);

        activeAccidents.put(accidentId, timerTask);
    }

    @Transactional
    public void handleUserResponse(Long accidentId, ResponseStatus response) {
        AccidentLog accidentLog = accidentLogRepository.findById(accidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Accident log not found"));

        // Cancel the timer if response received
        CompletableFuture<Void> timerTask = activeAccidents.remove(accidentId);
        if (timerTask != null && !timerTask.isDone()) {
            timerTask.cancel(true);
        }

        accidentLog.setResponse(response);
        accidentLogRepository.save(accidentLog);

        // If user responds NO, send alert immediately
        if (response == ResponseStatus.NO) {
            twilioService.sendEmergencyAlert(
                    accidentLog.getUser().getUserId(),
                    accidentLog.getLatitude(),
                    accidentLog.getLongitude()
            );
        }
        // If YES, just log it as safe (already saved above)
    }

    @Transactional
    public void sendAlert(Long accidentId) {
        AccidentLog accidentLog = accidentLogRepository.findById(accidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Accident log not found"));

        // Cancel the timer if alert is being sent manually
        CompletableFuture<Void> timerTask = activeAccidents.remove(accidentId);
        if (timerTask != null && !timerTask.isDone()) {
            timerTask.cancel(true);
        }

        twilioService.sendEmergencyAlert(
                accidentLog.getUser().getUserId(),
                accidentLog.getLatitude(),
                accidentLog.getLongitude()
        );
    }
}

