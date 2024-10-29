package com.biblioteca.bibliotecasenac.Component;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.biblioteca.bibliotecasenac.Model.Admim;
import com.biblioteca.bibliotecasenac.Repository.AdmimRepository;
import com.biblioteca.bibliotecasenac.Repository.AlunoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRepository;

@Component
public class Iniciar {

    @Autowired
    AdmimRepository admimRepository;
    @Autowired
    LivroRepository livroRepository;
    @Autowired
    AlunoRepository alunoRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private LocalDateTime dataAgora = LocalDateTime.now();

    @EventListener(ApplicationReadyEvent.class)
    public void SetarAdmin() {
        Admim admim = new Admim();
        admim.setId(1);
        admim.setNome("Administrador");
        admim.setUsuario("admin");
        admim.setSenha("123");
        admim.setSenha(passwordEncoder.encode(admim.getSenha()));
        admimRepository.save(admim);
    }

    // método de cancelar todas as reservas que são maiores que

}
