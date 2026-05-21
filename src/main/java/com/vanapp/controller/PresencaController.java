package com.vanapp.controller;

import com.vanapp.model.Presenca;
import com.vanapp.service.PresencaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/presenca")
@CrossOrigin(origins = "*")
public class PresencaController {
    @Autowired
    private PresencaService presencaService;

    @PostMapping("/confirmar")
    public void confirmar(@RequestBody Presenca presenca) {
        presencaService.registrarPresenca(presenca);
    }
}