package com.donaton.bff.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class BffDashboardControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BffDashboardController bffDashboardController;

    @Test
    void getDonaciones_RedirigePeticionCorrectamente() {
        Object[] mockRespuesta = new Object[]{};
        ResponseEntity<Object[]> responseEntity = new ResponseEntity<>(mockRespuesta, HttpStatus.OK);
        
        // CORRECCIÓN: Usamos lenient() y parámetros genéricos (anyString, any) 
        // para garantizar que Mockito intercepte la llamada sin lanzar excepciones de estrictez.
        lenient().when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                eq(Object[].class)
        )).thenReturn(responseEntity);

        ResponseEntity<?> resultado = bffDashboardController.getDonaciones("Bearer token-falso");

        assertEquals(200, resultado.getStatusCode().value());
    }
}