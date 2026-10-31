package com.example.jobbasics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobBasicsApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobBasicsApplication.class, args);
    }
}
