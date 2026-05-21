package com.vanapp.controller;

import com.vanapp.model.Usuario;
import com.vanapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> credenciais) {
        String cpf = credenciais.get("cpf");
        String senha = credenciais.get("senha");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);

        if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha)) {
            Usuario u = usuarioOpt.get();
            Map<String, String> resposta = new HashMap<>();
            resposta.put("id", u.getId().toString());
            resposta.put("tipo", u.getTipo());
            return ResponseEntity.ok(resposta);
        } else {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }
}