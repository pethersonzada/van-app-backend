package com.vanapp.controller;

import com.vanapp.model.Usuario;
import com.vanapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario loginRequest) {
        String cpfLimpo = loginRequest.getCpf().trim();
        String senhaLimpa = loginRequest.getSenha().trim();

        System.out.println("--- TENTATIVA DE LOGIN ---");
        System.out.println("CPF recebido: [" + cpfLimpo + "]");
        
        Optional<Usuario> usuario = usuarioRepository.findByCpf(cpfLimpo);
        
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            if (u.getSenha().trim().equals(senhaLimpa)) {
                System.out.println("Login com sucesso!");
                return ResponseEntity.ok(u);
            } else {
                System.out.println("Senha errada!");
            }
        } else {
            System.out.println("CPF não encontrado.");
        }
        
        return ResponseEntity.status(401).build();
    }
}