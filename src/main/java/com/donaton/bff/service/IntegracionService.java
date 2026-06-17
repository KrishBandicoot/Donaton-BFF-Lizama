package com.donaton.bff.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class IntegracionService {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${microservicio.donacion.url}")
    private String donacionUrl;

    @Value("${microservicio.logistica.url}")
    private String logisticaUrl;

    @Value("${microservicio.usuarios.url}")
    private String usuariosUrl;

    public IntegracionService(CircuitBreakerFactory circuitBreakerFactory) {
        this.restTemplate = new RestTemplate();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public List<Object> obtenerDonaciones() {
        return circuitBreakerFactory.create("donacionCircuitBreaker").run(
            () -> {
                Object[] donaciones = restTemplate.getForObject(donacionUrl, Object[].class);
                return Arrays.asList(donaciones);
            },
            throwable -> fallbackDonaciones()
        );
    }

    public List<Object> obtenerEnviosLogistica() {
        return circuitBreakerFactory.create("logisticaCircuitBreaker").run(
            () -> {
                Object[] envios = restTemplate.getForObject(logisticaUrl, Object[].class);
                return Arrays.asList(envios);
            },
            throwable -> fallbackLogistica()
        );
    }

    public ResponseEntity<?> login(Object loginRequest) {
        return circuitBreakerFactory.create("usuariosCircuitBreaker").run(
            () -> restTemplate.postForEntity(usuariosUrl + "/login", loginRequest, Object.class),
            throwable -> manejarErrorAutenticacion(throwable)
        );
    }

    public ResponseEntity<?> registrar(Object registroRequest) {
        return circuitBreakerFactory.create("usuariosCircuitBreaker").run(
            () -> restTemplate.postForEntity(usuariosUrl + "/register", registroRequest, Object.class),
            throwable -> manejarErrorAutenticacion(throwable)
        );
    }

    private ResponseEntity<?> manejarErrorAutenticacion(Throwable throwable) {
            if (throwable instanceof HttpClientErrorException) {
                HttpClientErrorException ex = (HttpClientErrorException) throwable;
                // Extrae el JSON de error exacto enviado por el microservicio de Usuarios y lo reenvía
                return ResponseEntity.status(ex.getStatusCode())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(ex.getResponseBodyAsString());
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Servicio de autenticación en mantenimiento o saturado."));
        }
    private List<Object> fallbackDonaciones() {
        return List.of("Servicio de Donaciones temporalmente no disponible. Reintente más tarde.");
    }

    private List<Object> fallbackLogistica() {
        return List.of("Servicio de Logística en mantenimiento o saturado.");
    }
}