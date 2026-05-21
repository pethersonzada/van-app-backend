package com.vanapp.controller;

import com.vanapp.model.Usuario;
import com.vanapp.service.RotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rota")
@CrossOrigin(origins = "*")
public class RotaController {

    @Autowired
    private RotaService rotaService;

    // Endpoint para buscar a rota otimizada apenas com passageiros confirmados
    @GetMapping("/otimizar/{motoristaId}")
    public ResponseEntity<?> otimizarRota(@PathVariable Long motoristaId) {
        try {
            // O RotaService agora filtra pela tabela de Presencas automaticamente
            List<Usuario> rota = rotaService.otimizarRota(motoristaId);
            return ResponseEntity.ok(rota);
        } catch (RuntimeException e) {
            // Se ninguém confirmou, retornamos a mensagem de erro formatada
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}