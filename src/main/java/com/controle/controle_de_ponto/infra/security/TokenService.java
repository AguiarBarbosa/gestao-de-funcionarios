package com.controle.controle_de_ponto.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT; // Importar
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException; // Importar
import com.auth0.jwt.exceptions.JWTVerificationException; // Importar
import com.controle.controle_de_ponto.domain.models.Funcionario; // Importar

@Service
public class TokenService {

    // A chave secreta será lida do application.properties
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(Funcionario funcionario) {
        try {
            // Algoritmo para assinar o token usando a chave secreta
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Criação do token
            return JWT.create()
                    .withIssuer("api-controle-ponto") // Emissor do token (sua API)
                    .withSubject(funcionario.getEmail()) // Assunto do token (geralmente o identificador do usuário)
                    .withExpiresAt(genExpirationDate()) // Data de expiração do token
                    .sign(algorithm); // Assina o token com o algoritmo e a chave
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("api-controle-ponto")
                    .build()
                    .verify(token) // Verifica o token
                    .getSubject(); // Retorna o assunto (email do usuário) se o token for válido
        } catch (JWTVerificationException exception){
            // Token inválido ou expirado
            return ""; // Retorna string vazia ou lança uma exceção customizada
        }
    }

    private Instant genExpirationDate() {
        // Token expira em 2 horas a partir de agora (GMT-3)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}