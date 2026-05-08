package com.donaton.bff.controller;

import com.donaton.bff.service.IntegracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
}