package com.umc.pediupatrao.config;

import com.umc.pediupatrao.entity.Perfil;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repo) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Usuários de teste para os três perfis exigidos pela atividade
            criarSeNaoExistir(repo, encoder, "admin", "teste123", Perfil.ADMIN);
            criarSeNaoExistir(repo, encoder, "gerente01", "teste123", Perfil.GERENTE);
            criarSeNaoExistir(repo, encoder, "atendente01", "teste123", Perfil.ATENDENTE);
        };
    }

    private void criarSeNaoExistir(UsuarioRepository repo, BCryptPasswordEncoder encoder,
            String username, String senha, Perfil perfil) {
        if (repo.findByUsername(username).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(encoder.encode(senha));
            usuario.setRole(perfil);
            repo.save(usuario);
            System.out.println("Usuário " + username + " (" + perfil + ") criado!");
        }
    }
}