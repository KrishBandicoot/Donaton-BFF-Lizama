package com.donaton.bff.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class IntegracionService {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${microservicio.donacion.url}")
    private String donacionUrl;

    @Value("${microservicio.logistica.url}")
    private String logisticaUrl;

    public IntegracionService(CircuitBreakerFactory circuitBreakerFactory) {
        this.restTemplate = new RestTemplate();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    // Llama al puerto 8081 protegido por Circuit Breaker
    public List<Object> obtenerDonaciones() {
        return circuitBreakerFactory.create("donacionCircuitBreaker").run(
            () -> {
                Object[] donaciones = restTemplate.getForObject(donacionUrl, Object[].class);
                return Arrays.asList(donaciones);
            },
            throwable -> fallbackDonaciones() // Método de contingencia si el 8081 falla
        );
    }

    // Llama al puerto 8082 protegido por Circuit Breaker
    public List<Object> obtenerEnviosLogistica() {
        return circuitBreakerFactory.create("logisticaCircuitBreaker").run(
            () -> {
                Object[] envios = restTemplate.getForObject(logisticaUrl, Object[].class);
                return Arrays.asList(envios);
            },
            throwable -> fallbackLogistica() // Método de contingencia si el 8082 falla
        );
    }

    // Fallbacks (Respuestas por defecto para que la app no se caiga)
    private List<Object> fallbackDonaciones() {
        return List.of("Servicio de Donaciones temporalmente no disponible. Reintente más tarde.");
    }

    private List<Object> fallbackLogistica() {
        return List.of("Servicio de Logística en mantenimiento o saturado.");
    }
}