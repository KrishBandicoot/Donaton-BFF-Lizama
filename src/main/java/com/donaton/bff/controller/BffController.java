package com.donaton.bff.controller;

import com.donaton.bff.service.IntegracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class BffController {

    @Autowired
    private IntegracionService integracionService;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenGeneral() {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("donaciones", integracionService.obtenerDonaciones());
        resumen.put("logistica", integracionService.obtenerEnviosLogistica());
        return ResponseEntity.ok(resumen);
    }
    
    @GetMapping("/opciones")
    public ResponseEntity<Map<String, List<String>>> obtenerOpcionesFormulario() {
        Map<String, List<String>> opciones = new HashMap<>();
        List<String> tipos = new ArrayList<>();
        List<String> centros = new ArrayList<>();
        List<String> destinos = new ArrayList<>();

        try {
            List<Map<String, Object>> catalogos = restTemplate.getForObject("http://localhost:8082/api/catalogo", List.class);
            if(catalogos != null) {
                for(Map<String, Object> cat : catalogos) {
                    String tipo = (String) cat.get("tipoCatalogo");
                    String valor = (String) cat.get("valor");
                    if("TIPO_AYUDA".equals(tipo)) tipos.add(valor);
                    else if("CENTRO_ACOPIO".equals(tipo)) centros.add(valor);
                    else if("DESTINO".equals(tipo)) destinos.add(valor);
                }
            }
        } catch(Exception e) { 
            System.out.println("Error leyendo catálogos: " + e.getMessage()); 
        }

        // Si la base de datos de catálogos está vacía, enviamos un valor por defecto para que el Home no se rompa
        opciones.put("tiposAyuda", tipos.isEmpty() ? Arrays.asList("Alimentos") : tipos);
        opciones.put("centrosAcopio", centros.isEmpty() ? Arrays.asList("Centro Principal") : centros);
        opciones.put("destinos", destinos.isEmpty() ? Arrays.asList("Refugio Base") : destinos);
        
        return ResponseEntity.ok(opciones);
    }
}