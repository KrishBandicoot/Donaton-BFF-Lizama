package com.donaton.bff.service;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class IntegracionServiceTest {

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private IntegracionService integracionService;

    @BeforeEach
    void setUp() {
        // Simulamos las variables del application.properties
        ReflectionTestUtils.setField(integracionService, "donacionUrl", "http://localhost:8081/api/donaciones");
        
        // Cada vez que el servicio intente crear un circuit breaker, devolvemos nuestro Mock
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
    }

    @Test
    void fallbackDonaciones_DebeRetornarMensajeDeErrorCuandoFalla() {
        // Arrange: Simulamos que el microservicio de Donaciones se cae.
        // Interceptamos el Circuit Breaker y forzamos a que ejecute el "throwable" (el fallback)
        when(circuitBreaker.run(any(Supplier.class), any(Function.class)))
                .thenAnswer(invocation -> {
                    Function<Throwable, List<Object>> fallback = invocation.getArgument(1);
                    // Disparamos el fallback simulando un error de conexión
                    return fallback.apply(new RuntimeException("Connection refused"));
                });

        // Act: El BFF intenta obtener donaciones
        List<Object> resultado = integracionService.obtenerDonaciones();

        // Assert: Comprobamos que el BFF no explotó (error 500), sino que devolvió tu mensaje de contingencia
        assertEquals(1, resultado.size());
        assertEquals("Servicio de Donaciones temporalmente no disponible. Reintente más tarde.", resultado.get(0));
    }
}