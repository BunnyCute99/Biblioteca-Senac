package com.biblioteca.bibliotecasenac.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.biblioteca.bibliotecasenac.Model.Admim;
import com.biblioteca.bibliotecasenac.Model.Aluno;
import com.biblioteca.bibliotecasenac.Model.Livro;
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

    // formato da data em dias/mes/ano, horas:minutos:segundos, dia da semana
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss, EEEE");

    // Evento que seta o admin inicial no id 1 do banco de dados
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

    // Método que Cancela Reservas que passaram do dia
    @EventListener(ApplicationReadyEvent.class)
    public void CancelarReserva() {
        // teste
        dataAgora = dataAgora.plusDays(5);
        List<Livro> livros = livroRepository.findByLimiteReservaLessThan(dataAgora);

        for (Livro livro : livros) {
            if (livro.isReservado()) {
                Aluno aluno = livro.getAluno();
                aluno.setNumeroLivros(aluno.getNumeroLivros() - 1);
                alunoRepository.save(aluno);

                livro.setReservado(false);
                // atualiza a data
                livro.setDataLivro("" + dataAgora.format(dateTimeFormatter));
                // limpa a data limite
                livro.setLimiteReserva(null);
                // limpa a string da data limite
                livro.setDataLimiteReserva(null);
                // Limpar a relação do Aluno com o Livro
                livro.setAluno(null);
                livroRepository.save(livro);
            }
        }

    }

}
