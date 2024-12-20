package com.biblioteca.bibliotecasenac.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Aluno;
import com.biblioteca.bibliotecasenac.Repository.AlunoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRepository;
import com.biblioteca.bibliotecasenac.Service.AlunoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    LivroRepository livroRepository;

    @Autowired
    AlunoService alunoService;

    @GetMapping("/registrar")
    public ModelAndView PagResgistrarAluno(HttpSession hSession) { // redireciona pra página de resgistro de aluno
        return alunoService.PagResgistrarAluno(hSession);
    }

    @PostMapping("/registrar")
    public ModelAndView ResgistrarAluno(Aluno aluno, String rsenha, HttpSession hSession) { // Registra alunos no banco
        return alunoService.ResgistrarAluno(aluno, rsenha, hSession);
    }

    @GetMapping("/listar") // Redireciona pra Pagina de Listar Alunos no Banco de Dados
    public ModelAndView PagListarAluno(HttpSession hSession) {
        return alunoService.PagListarAluno(hSession);
    }

    @GetMapping("/entrar")
    public String PagEntrarAluno() { // Redireciona para a página de Entrar do Aluno
        return "EntrarAluno";
    }

    @PostMapping("/entrar")
    public ModelAndView EntrarAluno(Aluno aluno, HttpSession hSession) { // Metodo de entrada do aluno no sistema
        return alunoService.EntrarAluno(aluno, hSession);
    }

    @GetMapping("/home¬aluno") // página home do aluno
    public ModelAndView PagHomeAluno(HttpSession hSession) {

        return alunoService.PagHomeAluno(hSession);

    }

    @GetMapping("/alugar")
    public ModelAndView PagAlugarLivroAluno(HttpSession hSession) { // get página de alugar livros
        return alunoService.PagAlugarLivroAluno(hSession);
    }

    @GetMapping("/reservar")
    public ModelAndView reservarLivroAluno(HttpSession hSession) {
        return alunoService.reservarLivroAluno(hSession);
    }

    // Buscar Livros como Aluno

    // buscar na aba de Alugar Livros
    @PostMapping("/buscarAlugar")
    public ModelAndView BuscarAlugarLivros(HttpSession hSession, String buscaLivro, String tipoBusca) {
        return alunoService.BuscarAlugarLivros(hSession, buscaLivro, tipoBusca);
    }

    // buscar na aba de Reservar Livros
    @PostMapping("/buscarReservar")
    public ModelAndView BuscarReservarLivros(HttpSession hSession, String buscaLivro, String tipoBusca) {
        return alunoService.BuscarReservarLivros(hSession, buscaLivro, tipoBusca);
    }

    // Editar e Deletar Alunos
    @GetMapping("/excluir/{id}")
    public ModelAndView DeletarAluno(@PathVariable("id") int id, HttpSession hSession) {
        return alunoService.DeletarAluno(hSession, id);
    }

    @GetMapping("/editar/{id}")
    public ModelAndView PagEditarAluno(@PathVariable("id") int id, HttpSession hSession) {
        return alunoService.PagEditar(hSession, id);
    }

    @PostMapping("/editar")
    public ModelAndView EditarAluno(Aluno aluno, HttpSession hSession, String rsenha) {
        return alunoService.EditarAluno(hSession, aluno, rsenha);
    }
}
