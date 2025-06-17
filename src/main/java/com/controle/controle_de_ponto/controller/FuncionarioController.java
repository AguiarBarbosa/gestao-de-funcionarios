package com.controle.controle_de_ponto.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Importe esta classe para formatar a saída
import java.util.ArrayList; // Importe esta classe, se ainda não estiver
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; 
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.controle.controle_de_ponto.domain.models.Funcionario;
import com.controle.controle_de_ponto.dto.AuthDTO;
import com.controle.controle_de_ponto.dto.LoginResponseDTO;
import com.controle.controle_de_ponto.infra.security.TokenService;
import com.controle.controle_de_ponto.repository.FuncionarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ponto")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getSenha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Pega o objeto Funcionario autenticado
        Funcionario funcionario = (Funcionario) auth.getPrincipal(); 

        var token = tokenService.generateToken(funcionario);
        System.out.println("DEBUG: Token JWT Gerado: " + token);
        
        // Use o novo construtor que inclui os dados do funcionário
        return ResponseEntity.ok(new LoginResponseDTO(token, funcionario)); 
    }

    @PostMapping
    public Funcionario salvar(@RequestBody Funcionario funcionario) {
        String senhaCriptografada = passwordEncoder.encode(funcionario.getSenha());
        funcionario.setSenha(senhaCriptografada);
        return funcionarioRepository.save(funcionario);
    }

    // NOVO ENDPOINT para bater o ponto
    @PostMapping("/{id}/bater")
    public ResponseEntity<String> baterPonto(@PathVariable Long id) {
        Optional<Funcionario> optionalFuncionario = funcionarioRepository.findById(id);

        if (optionalFuncionario.isPresent()) {
            Funcionario funcionario = optionalFuncionario.get();
            LocalDateTime agora = LocalDateTime.now(); // Pega a data e hora atual do servidor
            
            // **AJUSTE AQUI:** Garante que a lista 'pontos' não é nula
            if (funcionario.getPontos() == null) {
                funcionario.setPontos(new ArrayList<>());
            }
            
            funcionario.getPontos().add(agora); // Adiciona o ponto à lista
            funcionarioRepository.save(funcionario); // Salva o funcionário atualizado

            // Opcional: Formata a saída para ser mais amigável no frontend
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
            return ResponseEntity.ok("Ponto batido com sucesso para " + funcionario.getNome() + " às " + agora.format(formatter));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Funcionario> listar() {
        return funcionarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Long id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);

        if (funcionario.isPresent()) {
            return ResponseEntity.ok(funcionario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(@PathVariable Long id, @RequestBody Funcionario funcionarioDetalhes) {
        return funcionarioRepository.findById(id).map(funcionario -> {
            funcionario.setNome(funcionarioDetalhes.getNome());
            funcionario.setEmail(funcionarioDetalhes.getEmail());
            if (funcionarioDetalhes.getSenha() != null && !funcionarioDetalhes.getSenha().isEmpty()) {
                String novaSenhaCriptografada = passwordEncoder.encode(funcionarioDetalhes.getSenha());
                funcionario.setSenha(novaSenhaCriptografada);
            }
            funcionario.setAdministrador(funcionarioDetalhes.isAdministrador());

            Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);
            return ResponseEntity.ok(funcionarioAtualizado);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!funcionarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        funcionarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}