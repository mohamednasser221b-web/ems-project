package com.company.ems.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    // Liveness: "is the process running at all". Kept trivially cheap on
    // purpose - a slow liveness check causes Kubernetes to kill healthy pods
    // under load, which is the opposite of what you want.
    @GetMapping("/api/v1/health")
    public Map<String, String> liveness() {
        return Map.of("status", "UP");
    }

    // Readiness is handled by Spring Boot Actuator's /actuator/health/readiness
    // (checks DB connectivity via the datasource health indicator). This
    // endpoint is a simple alias kept for clients that expect the /api/v1
    // prefix consistently.
    @GetMapping("/api/v1/health/ready")
    public Map<String, String> readiness() {
        return Map.of("status", "UP");
    }
}
