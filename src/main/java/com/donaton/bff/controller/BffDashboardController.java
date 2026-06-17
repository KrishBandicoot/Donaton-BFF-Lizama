package com.donaton.bff.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class BffDashboardController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String DONACIONES_URL = "http://localhost:8081/api/donacion";
    private final String LOGISTICA_URL = "http://localhost:8082/api/envio";
    private final String USUARIOS_URL = "http://localhost:8083/api/usuarios";

    private HttpEntity<Object> empaquetarPeticion(String token, Object body) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", token);
        }
        if (body != null) {
            headers.set("Content-Type", "application/json");
        }
        return new HttpEntity<>(body, headers);
    }

    @GetMapping("/donaciones")
    public ResponseEntity<List> getDonaciones(@RequestHeader(value = "Authorization", required = false) String token) {
        // Usamos List.class para que el BFF reciba los datos como una lista genérica y no falle
        return restTemplate.exchange(DONACIONES_URL, HttpMethod.GET, empaquetarPeticion(token, null), List.class);
    }

    @DeleteMapping("/donaciones/{id}")
    public ResponseEntity<?> deleteDonacion(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        return restTemplate.exchange(DONACIONES_URL + "/" + id, HttpMethod.DELETE, empaquetarPeticion(token, null), Object.class);
    }

    @PutMapping("/donaciones/{id}")
    public ResponseEntity<?> updateDonacion(@PathVariable Long id, @RequestBody Object body, @RequestHeader(value = "Authorization", required = false) String token) {
        return restTemplate.exchange(DONACIONES_URL + "/" + id, HttpMethod.PUT, empaquetarPeticion(token, body), Object.class);
    }

    @GetMapping("/logistica")
    public ResponseEntity<?> getLogistica(@RequestHeader(value = "Authorization", required = false) String token) {
        return restTemplate.exchange(LOGISTICA_URL, HttpMethod.GET, empaquetarPeticion(token, null), Object[].class);
    }

    @DeleteMapping("/logistica/{id}")
    public ResponseEntity<?> deleteLogistica(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        return restTemplate.exchange(LOGISTICA_URL + "/" + id, HttpMethod.DELETE, empaquetarPeticion(token, null), Object.class);
    }

    @PutMapping("/logistica/{id}")
    public ResponseEntity<?> updateLogistica(@PathVariable Long id, @RequestBody Object body, @RequestHeader(value = "Authorization", required = false) String token) {
        return restTemplate.exchange(LOGISTICA_URL + "/" + id, HttpMethod.PUT, empaquetarPeticion(token, body), Object.class);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> getUsuarios(@RequestHeader(value = "Authorization", required = false) String token) {
        return restTemplate.exchange(USUARIOS_URL, HttpMethod.GET, empaquetarPeticion(token, null), Object[].class);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        return restTemplate.exchange(USUARIOS_URL + "/" + id, HttpMethod.DELETE, empaquetarPeticion(token, null), Object.class);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Object body, @RequestHeader(value = "Authorization", required = false) String token) {
        return restTemplate.exchange(USUARIOS_URL + "/" + id, HttpMethod.PUT, empaquetarPeticion(token, body), Object.class);
    }
}