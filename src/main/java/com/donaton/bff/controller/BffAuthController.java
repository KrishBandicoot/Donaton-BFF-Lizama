package com.donaton.bff.controller;

import com.donaton.bff.service.IntegracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class BffAuthController {

    @Autowired
    private IntegracionService integracionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Object request) {
        return integracionService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Object request) {
        return integracionService.registrar(request);
    }
}