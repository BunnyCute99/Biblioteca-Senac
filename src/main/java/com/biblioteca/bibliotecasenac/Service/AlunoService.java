package com.biblioteca.bibliotecasenac.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Aluno;
import com.biblioteca.bibliotecasenac.Model.Livro;
import com.biblioteca.bibliotecasenac.Repository.AlunoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AlunoService {

    @Autowired
    AlunoRepository alunoRepository;
    @Autowired
    LivroRepository livroRepository;

    @Autowired
    AdminService adminService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ModelAndView PagResgistrarAluno(HttpSession hSession) { // redireciona pra página de resgistro de aluno
        ModelAndView mv = new ModelAndView("RegistrarAluno");
        return adminService.AutentificarAdmim(hSession, mv);
    }

    public ModelAndView ResgistrarAluno(Aluno aluno, String rsenha, HttpSession hSession) { // Registra alunos no banco
        ModelAndView mv = new ModelAndView("RegistrarAluno");
        if (AutentificarAluno(hSession)) {
            if (aluno.getSenha().equals(rsenha)) {
                aluno.setNumeroLivros(0); // seta o contador de livros do aluno pra 0
                aluno.setSenha(passwordEncoder.encode(aluno.getSenha())); // criptogra a senha
                alunoRepository.save(aluno); // salva aluno no banco
                // mensagem de aviso que deu tudo certo
                mv.addObject("certo", "Usuário registrado com sucesso");

            } else {
                // mensagem de aviso que deu erro
                mv.addObject("erro", "senha não são iguais");
            }
        } else {
            mv.setViewName("EntrarAluno");
        }

        return mv;
    }

    // Redireciona pra Pagina de Listar Alunos no Banco de Dados
    public ModelAndView PagListarAluno(HttpSession hSession) {
        ModelAndView mv = new ModelAndView("ListaAluno");

        List<Aluno> alunos = alunoRepository.findAll(Sort.by(Sort.Direction.ASC, "id")); // ordena a lista por id
        mv.addObject("alunos", alunos);

        return adminService.AutentificarAdmim(hSession, mv);
    }

    // cógios pra procurar um aluno em uma secção

    public ModelAndView AutentificarAluno(HttpSession hSession, ModelAndView mv) {
        Aluno aluno = (Aluno) hSession.getAttribute("aluno");
        if (aluno == null) {
            mv.setViewName("EntrarAluno");
        }
        return mv;
    }

    public boolean AutentificarAluno(HttpSession hSession) {
        Aluno aluno = (Aluno) hSession.getAttribute("aluno");
        if (aluno == null) {
            return false;
        } else {
            return true;
        }
    }

    public ModelAndView EntrarAluno(Aluno aluno, HttpSession hSession) { // Metodo de entrada do aluno no sistema

        ModelAndView mv;
        // se o repositorio tiver algum aluno com o id aOptional será preenchido com
        // ele, se não ele vai ter um resultado nullo
        Optional<Aluno> aOptional = alunoRepository.findById(aluno.getId());

        // se existe um aluno com esse id e a senha dele bate com a que foi digitada
        if (aOptional.isPresent() && passwordEncoder.matches(aluno.getSenha(), aOptional.get().getSenha())) {

            mv = new ModelAndView("HomeAluno"); // paǵina que será aberto pelo Model
            mv.addObject("aluno", aOptional.get()); // manda o aluno pra view

            hSession.setAttribute("aluno", aOptional.get()); // manda o aluno para ele poder passar em uma seção
            hSession.setMaxInactiveInterval(20 * 60); // intervalo de inatividade que se pode ter em uma seção

        } else {
            mv = new ModelAndView("EntrarAluno"); // volta pra pagina de login com mensagem de erro
            mv.addObject("erro", "erro no login número da matrícula ou senha incorretos");
        }

        return mv;

    }

    // get da home page do aluno
    public ModelAndView PagHomeAluno(HttpSession hSession) {

        ModelAndView mv = new ModelAndView("HomeAluno");

        Aluno aluno = (Aluno) hSession.getAttribute("aluno");
        mv.addObject("aluno", aluno);

        return AutentificarAluno(hSession, mv);

    }

    // página de alugar livros, as ações de alugar e devolver estão no LivroService

    public ModelAndView PagAlugarLivroAluno(HttpSession hSession) {

        ModelAndView mv = new ModelAndView("AlugarLivros");
        Aluno aluno = (Aluno) hSession.getAttribute("aluno");

        List<Livro> livrosDisponiveis = livroRepository.findByEmprestado(false);
        List<Livro> livrosAlugados = livroRepository.findByEmprestadoAndAluno(true, aluno);

        mv.addObject("livros", livrosDisponiveis);
        mv.addObject("livrosAlugados", livrosAlugados);
        mv.addObject("aluno", aluno);

        return AutentificarAluno(hSession, mv);
    }

}
