package com.accidentdetection.accident.detection.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccidentDetectionBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccidentDetectionBackendApplication.class, args);
        System.out.println("Accident Detection Backend in live");
	}

}
