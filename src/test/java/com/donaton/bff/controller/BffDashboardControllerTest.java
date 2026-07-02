package com.donaton.bff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class BffDashboardControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BffDashboardController bffDashboardController;

    @BeforeEach
    void setUp() {
        // Inyectamos el RestTemplate a la fuerza
        ReflectionTestUtils.setField(bffDashboardController, "restTemplate", restTemplate);
    }

    private void mockRestTemplateExchange() {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(new Object[]{}, HttpStatus.OK);
        lenient().when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                any(Class.class)
        )).thenReturn(responseEntity);
    }

    @Test
    void endpointsCatalogos_DeberianRetornar200() {
        mockRestTemplateExchange();
        assertEquals(200, bffDashboardController.getCatalogos("token").getStatusCode().value());
        assertEquals(200, bffDashboardController.createCatalogo(new Object(), "token").getStatusCode().value());
        assertEquals(200, bffDashboardController.deleteCatalogo(1L, "token").getStatusCode().value());
    }

    @Test
    void endpointsDonaciones_DeberianRetornar200() {
        mockRestTemplateExchange();
        assertEquals(200, bffDashboardController.getDonaciones("token").getStatusCode().value());
        assertEquals(200, bffDashboardController.updateDonacion(1L, new Object(), "token").getStatusCode().value());
        assertEquals(200, bffDashboardController.deleteDonacion(1L, null).getStatusCode().value());
    }

    @Test
    void endpointsLogistica_DeberianRetornar200() {
        mockRestTemplateExchange();
        assertEquals(200, bffDashboardController.getLogistica("token").getStatusCode().value());
        assertEquals(200, bffDashboardController.updateLogistica(1L, new Object(), "token").getStatusCode().value());
        assertEquals(200, bffDashboardController.deleteLogistica(1L, "token").getStatusCode().value());
    }

    @Test
    void endpointsUsuarios_DeberianRetornar200() {
        mockRestTemplateExchange();
        assertEquals(200, bffDashboardController.getUsuarios("token").getStatusCode().value());
        assertEquals(200, bffDashboardController.updateUsuario(1L, new Object(), "token").getStatusCode().value());
        assertEquals(200, bffDashboardController.deleteUsuario(1L, "token").getStatusCode().value());
    }
}