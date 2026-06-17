package com.donaton.proyecto;

import com.donaton.bff.service.IntegracionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntegracionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @InjectMocks
    private IntegracionService integracionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testObtenerDonaciones_FallbackActivado() {
        CircuitBreaker mockCb = mock(CircuitBreaker.class);
        when(circuitBreakerFactory.create(anyString())).thenReturn(mockCb);

        when(mockCb.run(any(Supplier.class), any(Function.class))).thenAnswer(invocation -> {
            Function<Throwable, Object> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Simulando una caída del microservicio"));
        });

        List<Object> resultado = integracionService.obtenerDonaciones();

        assertEquals(1, resultado.size());
        assertEquals("Servicio de Donaciones temporalmente no disponible. Reintente más tarde.", resultado.get(0));
    }
}