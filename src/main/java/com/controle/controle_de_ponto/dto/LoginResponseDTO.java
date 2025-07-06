package com.controle.controle_de_ponto.dto;

import com.controle.controle_de_ponto.domain.models.Funcionario; 

public record LoginResponseDTO(
    String token,
    Long id,          
    String nome,      
    String email,   
    boolean administrador 
) {

    public LoginResponseDTO(String token, Funcionario funcionario) {
        this(token, funcionario.getId(), funcionario.getNome(), funcionario.getEmail(), funcionario.isAdministrador());
    }
}