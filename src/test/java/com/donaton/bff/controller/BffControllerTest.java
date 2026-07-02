package com.donaton.bff.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.donaton.bff.service.IntegracionService;

class BffControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IntegracionService integracionService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BffController bffController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inyectamos el RestTemplate a la fuerza
        ReflectionTestUtils.setField(bffController, "restTemplate", restTemplate);
        mockMvc = MockMvcBuilders.standaloneSetup(bffController).build();
    }

    @Test
    void obtenerResumenGeneral_DebeRetornar200() throws Exception {
        when(integracionService.obtenerDonaciones()).thenReturn(Collections.emptyList());
        when(integracionService.obtenerEnviosLogistica()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/dashboard/resumen")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerOpcionesFormulario_DebeRetornar200() throws Exception {
        List<Map<String, Object>> mockCatalogos = List.of(
                Map.of("tipoCatalogo", "TIPO_AYUDA", "valor", "Ropa"),
                Map.of("tipoCatalogo", "CENTRO_ACOPIO", "valor", "Sede Central"),
                Map.of("tipoCatalogo", "DESTINO", "valor", "Sur")
        );
        
        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(mockCatalogos);

        mockMvc.perform(get("/api/dashboard/opciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerOpcionesFormulario_ConExcepcion_DebeRetornarDefectos() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(List.class))).thenThrow(new RuntimeException("Simulacion Servidor Caido"));

        mockMvc.perform(get("/api/dashboard/opciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}