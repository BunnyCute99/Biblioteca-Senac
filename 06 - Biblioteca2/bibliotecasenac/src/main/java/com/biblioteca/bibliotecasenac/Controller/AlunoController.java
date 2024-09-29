package com.biblioteca.bibliotecasenac.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Aluno;
import com.biblioteca.bibliotecasenac.Model.Livro;
import com.biblioteca.bibliotecasenac.Repository.AlunoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    LivroRepository livroRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/registrar")
    public String PagResgistarAluno() { // redireciona pra página de resgistro de aluno
        return "RegistrarAluno";
    }

    @PostMapping("/registrar")
    public String ResgistarAluno(Aluno aluno, String rsenha, Model model) { // Registra alunos no banco de dados
        if (aluno.getSenha().equals(rsenha)) {
            aluno.setNumeroLivros(0); // seta o contador de livros do aluno pra 0
            aluno.setSenha(passwordEncoder.encode(aluno.getSenha()));
            alunoRepository.save(aluno);
        } else {
            model.addAttribute("erro", "erro");
        }
        return "RegistrarAluno";
    }

    @GetMapping("/listar")
    public ModelAndView PagListarAluno() {// Redireciona pra Pagina de Listar Alunos no Banco de Dados
        ModelAndView mv = new ModelAndView("ListaAluno");
        List<Aluno> alunos = alunoRepository.findAll(Sort.by(Sort.Direction.ASC, "id")); // ordena a lista por id
        mv.addObject("alunos", alunos);
        return mv;
    }

    @GetMapping("/entrar")
    public String PagEntrarAluno() { // Redireciona para a página de Entrar com id do Aluno
        return "EntrarAluno";
    }

    @PostMapping("/entrar")
    public ModelAndView EntrarAluno(Aluno aluno, HttpSession hSession) { // Metodo de entrada do aluno no sistema

        ModelAndView mv;

        Optional<Aluno> aOptional = alunoRepository.findById(aluno.getId());
        // se o repositorio tiver algum aluno com o id aOptional será preenchido com
        // ele, se não ele vai ter um resultado nullo

        if (aOptional.isPresent() && passwordEncoder.matches(aluno.getSenha(), aOptional.get().getSenha())) {
            // se existe um aluno com esse id e a senha dele bate com a que foi digitada

            mv = new ModelAndView("HomeAluno"); // paǵina que será aberto pelo Model
            mv.addObject("aluno", aOptional.get()); // manda o aluno pra view

            hSession.setAttribute("aluno", aOptional.get()); // manda o aluno para ele poder passar em uma seção
            hSession.setMaxInactiveInterval(20 * 60); // intervalo de inatividade que se pode ter em uma seção

        } else {
            mv = new ModelAndView("EntrarAluno"); // volta pra pagina de login
            boolean erro = true;
            System.out.println(erro);
            mv.addObject("erro", erro);
        }

        return mv;

    }

    @GetMapping("/alugar")
    public ModelAndView AlugarLivroAluno(HttpSession hSession) {

        ModelAndView mv = new ModelAndView();
        Aluno aluno = (Aluno) hSession.getAttribute("aluno");

        if (aluno == null) {
            mv.setViewName("EntrarAluno");
        } else {
            mv.setViewName("AlugarLivros");

            List<Livro> livrosDisponiveis = livroRepository.findByEmprestado(false);
            List<Livro> livrosAlugados = livroRepository.findByEmprestadoAndAluno(true, aluno);

            mv.addObject("livros", livrosDisponiveis);
            mv.addObject("livrosAlugados", livrosAlugados);
            mv.addObject("aluno", aluno);
        }

        return mv;
    }

}
