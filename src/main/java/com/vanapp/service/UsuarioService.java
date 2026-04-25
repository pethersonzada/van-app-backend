package com.vanapp.service;

import com.vanapp.model.Usuario;
import com.vanapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GeocodingService geocodingService;

    public Usuario cadastrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByCpf(usuario.getCpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado");
        }
        if (usuarioRepository.findByTelefone(usuario.getTelefone()).isPresent()) {
            throw new RuntimeException("Telefone já cadastrado");
        }
        double[] coordenadas = geocodingService.geocodificarEndereco(usuario.getEnderecoCompleto());
        usuario.setLatitude(coordenadas[0]);
        usuario.setLongitude(coordenadas[1]);
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<Usuario> listarPassageiros() {
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getTipo().equals("PASSAGEIRO"))
                .toList();
    }
}