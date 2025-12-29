package com.accidentdetection.accident.detection.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "accident_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccidentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accidentId;   // primary key

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;   // link accident log to a user

    @Column(nullable = false)
    private LocalDateTime timestamp;   // when accident was detected

    @Column
    private Double latitude;   // accident latitude

    @Column
    private Double longitude;  // accident longitude

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResponseStatus response;   // user response status (YES, NO, NO_RESPONSE)

    @Column(length = 500)
    private String description;   // optional details about the accident
}