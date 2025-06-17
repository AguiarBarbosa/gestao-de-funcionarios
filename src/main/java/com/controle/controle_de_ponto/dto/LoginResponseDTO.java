package com.controle.controle_de_ponto.dto;

import com.controle.controle_de_ponto.domain.models.Funcionario; // Importe a classe Funcionario

public record LoginResponseDTO(
    String token,
    Long id,          // Adicione o ID do funcionário
    String nome,      // Adicione o nome do funcionário
    String email,     // Adicione o email do funcionário
    boolean administrador // Adicione o status de administrador
) {
    // Construtor adicional para facilitar a criação a partir de um Funcionario
    // Este construtor será usado no seu FuncionarioController
    public LoginResponseDTO(String token, Funcionario funcionario) {
        this(token, funcionario.getId(), funcionario.getNome(), funcionario.getEmail(), funcionario.isAdministrador());
    }
}