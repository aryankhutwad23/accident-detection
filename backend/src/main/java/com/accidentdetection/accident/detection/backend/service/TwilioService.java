package com.accidentdetection.accident.detection.backend.service;

import com.accidentdetection.accident.detection.backend.entity.EmergencyContact;
import com.accidentdetection.accident.detection.backend.repository.EmergencyContactRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private final EmergencyContactRepository emergencyContactRepository;
    private boolean initialized = false;

    public TwilioService(EmergencyContactRepository emergencyContactRepository) {
        this.emergencyContactRepository = emergencyContactRepository;
    }

    @PostConstruct
    public void initTwilio() {
        if (accountSid != null && authToken != null &&
                !accountSid.isBlank() && !authToken.isBlank()) {
            Twilio.init(accountSid, authToken);
            initialized = true;
            log.info("🚀 Twilio initialized successfully!");
        } else {
            log.error("❌ Twilio credentials missing in application.properties");
        }
    }

    public String sendEmergencyAlert(Long userId, Double latitude, Double longitude) {

        if (!initialized) initTwilio();

        List<EmergencyContact> contacts = emergencyContactRepository.findByUserUserId(userId);

        if (contacts.isEmpty()) {
            throw new RuntimeException("No emergency contacts found for this user!");
        }

        EmergencyContact contact = contacts.get(0);

        String messageBody = String.format(
                "🚨 Accident Detected!\n📍 Location: https://maps.google.com/?q=%.6f,%.6f\nPlease contact immediately.",
                latitude, longitude
        );

        sendSMS(contact.getContactNumber1(), messageBody);

        if (contact.getContactNumber2() != null && !contact.getContactNumber2().isBlank()) {
            sendSMS(contact.getContactNumber2(), messageBody);
        }

        return "Alert sent successfully to saved emergency contacts!";
    }

    private void sendSMS(String toPhoneNumber, String messageBody) {
        try {
            Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    messageBody
            ).create();

            log.info("📩 SMS successfully sent to {}", toPhoneNumber);

        } catch (Exception e) {
            log.error("❌ Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());
        }
    }
}
