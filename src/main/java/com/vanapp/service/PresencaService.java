package com.vanapp.service;

import com.vanapp.model.Presenca;
import com.vanapp.repository.PresencaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class PresencaService {
    @Autowired
    private PresencaRepository presencaRepository;

    public void registrarPresenca(Presenca presenca) {
        presenca.setData(LocalDate.now());
        presencaRepository.save(presenca);
    }
}