package com.donaton.bff.controller;

import com.donaton.bff.service.IntegracionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BffControllerTest {

    @Mock
    private IntegracionService integracionService;

    @InjectMocks
    private BffController bffController;

    @Test
    void obtenerResumenGeneral_AgrupaDatosCorrectamente() {
        when(integracionService.obtenerDonaciones()).thenReturn(Arrays.asList("Donacion 1"));
        when(integracionService.obtenerEnviosLogistica()).thenReturn(Arrays.asList("Envio 1"));

        ResponseEntity<Map<String, Object>> response = bffController.obtenerResumenGeneral();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("donaciones"));
        assertTrue(response.getBody().containsKey("logistica"));
    }
}