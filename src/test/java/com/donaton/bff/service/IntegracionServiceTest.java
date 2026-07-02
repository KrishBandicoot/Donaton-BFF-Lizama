package com.donaton.bff.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class IntegracionServiceTest {

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IntegracionService integracionService;

    @BeforeEach
    void setUp() {
        // Configuramos las URLs para que no sean nulas
        ReflectionTestUtils.setField(integracionService, "donacionUrl", "http://localhost:8081/api/donacion");
        ReflectionTestUtils.setField(integracionService, "logisticaUrl", "http://localhost:8082/api/envio");
        ReflectionTestUtils.setField(integracionService, "usuariosUrl", "http://localhost:8083/api/usuarios");
        
        // Inyectamos el RestTemplate a la fuerza para que Mockito funcione
        ReflectionTestUtils.setField(integracionService, "restTemplate", restTemplate);
        
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
    }

    @Test
    void obtenerDonaciones_Exito() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Supplier<List<Object>> supplier = invocation.getArgument(0);
            return supplier.get();
        });
        Object[] donacionesMock = {new Object(), new Object()};
        when(restTemplate.getForObject(anyString(), eq(Object[].class))).thenReturn(donacionesMock);

        List<Object> resultado = integracionService.obtenerDonaciones();
        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerDonaciones_Fallback() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Function<Throwable, List<Object>> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Error BD"));
        });

        List<Object> resultado = integracionService.obtenerDonaciones();
        assertEquals("Servicio de Donaciones temporalmente no disponible. Reintente más tarde.", resultado.get(0));
    }

    @Test
    void obtenerEnviosLogistica_Exito() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Supplier<List<Object>> supplier = invocation.getArgument(0);
            return supplier.get();
        });
        Object[] enviosMock = {new Object()};
        when(restTemplate.getForObject(anyString(), eq(Object[].class))).thenReturn(enviosMock);

        List<Object> resultado = integracionService.obtenerEnviosLogistica();
        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerEnviosLogistica_Fallback() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Function<Throwable, List<Object>> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Error Timeout"));
        });

        List<Object> resultado = integracionService.obtenerEnviosLogistica();
        assertEquals("Servicio de Logística en mantenimiento o saturado.", resultado.get(0));
    }

    @Test
    void login_Exito() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Supplier<ResponseEntity<?>> supplier = invocation.getArgument(0);
            return supplier.get();
        });
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(Map.of("token", "12345"), HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class))).thenReturn(responseEntity);

        ResponseEntity<?> resultado = integracionService.login(new Object());
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
    }

    @Test
    void login_Fallback_HttpClientError() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Function<Throwable, ResponseEntity<?>> fallback = invocation.getArgument(1);
            byte[] body = "{\"error\":\"Credenciales invalidas\"}".getBytes(StandardCharsets.UTF_8);
            return fallback.apply(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", body, StandardCharsets.UTF_8));
        });

        ResponseEntity<?> resultado = integracionService.login(new Object());
        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());
        assertTrue(resultado.getBody().toString().contains("Credenciales invalidas"));
    }

    @Test
    void registrar_Fallback_OtroError() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Function<Throwable, ResponseEntity<?>> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Connection refused"));
        });

        ResponseEntity<?> resultado = integracionService.registrar(new Object());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, resultado.getStatusCode());
    }
}