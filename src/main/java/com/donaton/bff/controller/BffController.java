package com.donaton.bff.controller;

import com.donaton.bff.service.IntegracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenGeneral() {
        Map<String, Object> resumen = new HashMap<>();
        
        // El BFF orquesta la llamada a ambos microservicios
        resumen.put("donaciones", integracionService.obtenerDonaciones());
        resumen.put("logistica", integracionService.obtenerEnviosLogistica());
        
        return ResponseEntity.ok(resumen);
    }
    @GetMapping("/opciones")
    public ResponseEntity<Map<String, List<String>>> obtenerOpcionesFormulario() {
        Map<String, List<String>> opciones = new HashMap<>();
        
        opciones.put("tiposAyuda", Arrays.asList("Alimento", "Ropa de Abrigo", "Insumos Médicos", "Insumos de Higiene", "Materiales de Construcción"));
        opciones.put("centrosAcopio", Arrays.asList("Centro Central Santiago", "Sede Valparaíso", "Gimnasio Concepción", "Municipalidad de Maipú"));
        opciones.put("destinos", Arrays.asList("Refugio Maipú", "Campamento Viña del Mar", "Zona Cero Sur", "Albergue Estadio Nacional"));
        
        return ResponseEntity.ok(opciones);
    }
}