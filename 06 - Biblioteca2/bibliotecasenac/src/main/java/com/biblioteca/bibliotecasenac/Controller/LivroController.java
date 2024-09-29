package com.biblioteca.bibliotecasenac.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Aluno;
import com.biblioteca.bibliotecasenac.Model.Livro;
import com.biblioteca.bibliotecasenac.Model.LivroRecebido;
import com.biblioteca.bibliotecasenac.Repository.AlunoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRecebidoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/livro")
public class LivroController {

    @Autowired
    LivroRepository repository;

    @Autowired
    LivroRecebidoRepository livroRecebidoRepository;

    @Autowired
    AlunoRepository alunoRepository;

    DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss, EEEE"); // formatação de data

    @GetMapping("/resgistro") // pagina de registro
    public String PagResgistroLivro() {
        return "ResgistrarLivro";
    }

    @PostMapping("/resgistro") // registrar livro
    public String RegistrarLivro(Livro livro) {

        livro.setEmprestado(false);

        LocalDateTime date = LocalDateTime.now();
        String livrodate = "" + date.format(dFormatter);
        livro.setDataLivro(livrodate);

        repository.save(livro);
        return "redirect:/livro/resgistro";
    }

    @GetMapping("/estoque") // página com a cópia do repositório completo
    public ModelAndView PagListarEstoque() {
        ModelAndView mv = new ModelAndView("EstoqueLivros");
        List<Livro> livros = new ArrayList<>();
        livros = repository.findAll(Sort.by(Sort.Direction.ASC, "nomeLivro"));
        mv.addObject("livros", livros);
        return mv;
    }

    @GetMapping("/exluir/{codigoLivro}") // botão de excluir Livro por id
    public String ExluirLivro(@PathVariable("codigoLivro") String codigoLivro) {
        Livro livro = repository.findById(codigoLivro).get();
        if (livro.getAluno() != null) { // se o livro ter uma associação com algum aluno
            Aluno aluno = alunoRepository.findById(livro.getAluno().getId()).get();
            aluno.setNumeroLivros(aluno.getNumeroLivros() - 1);
            alunoRepository.save(aluno);
        }

        repository.deleteById(codigoLivro);
        return "redirect:/livro/estoque";
    }

    @GetMapping("/alugar/{codigoLivro}") // botão de alugar por id (sem aluno) [é pra debug]
    public String AlugarLivro(@PathVariable("codigoLivro") String codigoLivro) {

        Livro livro = repository.findById(codigoLivro).get();
        livro.setEmprestado(true);

        LocalDateTime date = LocalDateTime.now();
        String livrodate = "" + date.format(dFormatter);
        livro.setDataLivro(livrodate);

        repository.save(livro);

        return "redirect:/livro/estoque";
    }

    @GetMapping("/devolver/{codigoLivro}") // botão de devolver por id (sem aluno) [é pra debug]
    public String DevolverLivro(@PathVariable("codigoLivro") String codigoLivro) {

        Livro livro = repository.findById(codigoLivro).get(); // cria uma cópia do livro com código enviado

        if (livro.getAluno() != null) {
            Aluno aluno = alunoRepository.findById(livro.getAluno().getId()).get();
            aluno.setNumeroLivros(aluno.getNumeroLivros() - 1); // diminue o número de livros
            alunoRepository.save(aluno);
        }

        if (livro.isEmprestado()) {

            livro.setEmprestado(false); // seta a variavel devolta pra false

            LocalDateTime date = LocalDateTime.now(); // atualiza data e hora
            String livrodate = "" + date.format(dFormatter);
            livro.setDataLivro(livrodate);

            livro.setAluno(null); // limpa a relação com aluno
            repository.save(livro); // salva devolta no repositorio

            LivroRecebido livroRecebido = new LivroRecebido(); // cria um livro recebido com as inf do livro
            livroRecebido.setCodigoLivro(livro.getCodigoLivro());
            livroRecebido.setDataLivroRecebida(livro.getDataLivro());
            livroRecebido.setNomeLivro(livro.getNomeLivro());

            if (livro.getAluno() != null) {
                livroRecebido.setNomeAluno(livro.getAluno().getNome());
                livroRecebido.setIdAluno(livro.getAluno().getId());
            }

            livroRecebidoRepository.save(livroRecebido); // salva livro recebido num repositorio diferente
        }

        return "redirect:/livro/estoque";
    }

    @GetMapping("/emprestados") // lista de Alugados
    public ModelAndView PagListarEmprestados() {
        ModelAndView mv = new ModelAndView("LivrosAlugados");

        List<Livro> livrosAlugados = repository.findByEmprestado(true); // acha livros que estejam emprestados
        mv.addObject("livros", livrosAlugados);
        return mv;
    }

    @GetMapping("/disponiveis") // lista de Disponíveis
    public ModelAndView PagListarDisponiveis() {
        ModelAndView mv = new ModelAndView("LivrosDisponiveis");

        List<Livro> livrosDisponiveis = repository.findByEmprestado(false); // acha livros ques não estam emprestados

        mv.addObject("livros", livrosDisponiveis);
        return mv;
    }

    @GetMapping("/devolucoes") // pagina de Log de Devoluções
    public ModelAndView ListaLivrosDevolvidos() {
        ModelAndView mv = new ModelAndView("LogLivrosRecebidos");

        List<LivroRecebido> lRecebidos = livroRecebidoRepository.findAll();

        mv.addObject("livros", lRecebidos);

        return mv;
    }

    @GetMapping("/devolucoes/exluirtudo") // excluir todo o log de livros devolvidos
    public String ExcluirListaDevolvidos() {

        livroRecebidoRepository.deleteAll();

        return "redirect:/livro/devolucoes";
    }

    @GetMapping("/devolucoes/exluiritem/{ordem}") // excluir um item dos livros devolvidos
    public String ExcluirLivroDevolvidos(@PathVariable("ordem") int ordem) {

        livroRecebidoRepository.deleteById(ordem);

        return "redirect:/livro/devolucoes";
    }

    @GetMapping("/editar/{codigoLivro}") // editar livros
    public String EditarLivro(@PathVariable("codigoLivro") String codigoLivro, Model model) {
        Livro livro = repository.findById(codigoLivro).get();
        model.addAttribute("livro", livro);
        return "EditarLivro";
    }

    // Secção pra alugar e devolver livros relacionando eles com Aluno

    @GetMapping("/alugarAluno/{codigoLivro}") // botão de alugar por id com o aluno
    public ModelAndView AlugarLivroAluno(@PathVariable("codigoLivro") String codigoLivro, HttpSession hSession) {

        ModelAndView mv = new ModelAndView();
        Aluno aluno = (Aluno) hSession.getAttribute("aluno");

        if (aluno != null) { // se aluno esta autenticado

            mv = new ModelAndView("AlugarLivros");
            Livro livro = repository.findById(codigoLivro).get();

            // se o livro não está emprestado e o aluno possuí menos de 5 livro alugados
            if (aluno.getNumeroLivros() < 5 && livro.isEmprestado() == false) {

                // atualiza o estado do livro
                livro.setEmprestado(true);
                livro.setAluno(alunoRepository.findById(aluno.getId()).get());
                // atualiza a data hora
                LocalDateTime date = LocalDateTime.now();
                String livrodate = "" + date.format(dFormatter);
                livro.setDataLivro(livrodate);
                // salva no repositório
                repository.save(livro);

                // atualiza o estado do aluno no repositório e na seção
                aluno.setNumeroLivros(aluno.getNumeroLivros() + 1);
                alunoRepository.save(aluno);
                hSession.setAttribute("aluno", aluno);

            } else {
                boolean erro = true;
                mv.addObject("erro", erro);
            }

            List<Livro> livrosDisponiveis = repository.findByEmprestado(false); // lista de livros disponiveis
            List<Livro> livrosAlugados = repository.findByEmprestadoAndAluno(true, aluno); // lista de livros alugados
                                                                                           // pelo aluno

            mv.addObject("aluno", aluno);
            mv.addObject("livros", livrosDisponiveis);
            mv.addObject("livrosAlugados", livrosAlugados);

        } else {
            mv.setViewName("EntrarAluno");
        }

        return mv;
    }

    @GetMapping("/devolverLivro/{codigoLivro}") // devolver livro alugados por um aluno
    public ModelAndView DevolverLivroAluno(@PathVariable("codigoLivro") String codigoLivro, HttpSession hSession) {

        ModelAndView mv = new ModelAndView();
        Aluno aluno = (Aluno) hSession.getAttribute("aluno");

        if (aluno != null) {

            mv.setViewName("AlugarLivros");
            Livro livro = repository.findById(codigoLivro).get();

            if (livro.isEmprestado()) {

                livro.setEmprestado(false);

                LocalDateTime date = LocalDateTime.now(); // atualiza data e hora
                String livrodate = "" + date.format(dFormatter);
                livro.setDataLivro(livrodate);

                LivroRecebido livroRecebido = new LivroRecebido(); // entrada do log de livro
                livroRecebido.setCodigoLivro(livro.getCodigoLivro());
                livroRecebido.setDataLivroRecebida(livro.getDataLivro());
                livroRecebido.setNomeLivro(livro.getNomeLivro());
                livroRecebido.setNomeAluno(livro.getAluno().getNome());
                livroRecebido.setIdAluno(livro.getAluno().getId());

                livroRecebidoRepository.save(livroRecebido); // salva emtrada do log

                livro.setAluno(null); // limpa a relação com aluno do livro
                repository.save(livro);

                aluno.setNumeroLivros(aluno.getNumeroLivros() - 1); // atualiza o aluno
                alunoRepository.save(aluno);
                hSession.setAttribute("aluno", aluno);

            } else {
                boolean erro = true;
                mv.addObject("erro", erro);
            }

            List<Livro> livrosDisponiveis = repository.findByEmprestado(false); // lista de livros disponíveis
            List<Livro> livrosAlugados = repository.findByEmprestadoAndAluno(true, aluno); // lista de livros alugados
                                                                                           // pelo aluno

            mv.addObject("aluno", aluno);
            mv.addObject("livros", livrosDisponiveis);
            mv.addObject("livrosAlugados", livrosAlugados);

        } else {
            mv.setViewName("EntrarAluno");
        }

        return mv;
    }

}
