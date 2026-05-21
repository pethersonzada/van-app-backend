package com.vanapp.repository;

import com.vanapp.model.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PresencaRepository extends JpaRepository<Presenca, Long> {
    List<Presenca> findByDataAndStatus(LocalDate data, String status);
}