package com.controle.controle_de_ponto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.controle.controle_de_ponto.domain.models.Funcionario;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByEmail(String email);
}