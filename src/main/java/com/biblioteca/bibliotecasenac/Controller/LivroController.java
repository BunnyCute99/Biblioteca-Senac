package com.biblioteca.bibliotecasenac.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Livro;
import com.biblioteca.bibliotecasenac.Repository.AlunoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRecebidoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRepository;
import com.biblioteca.bibliotecasenac.Service.LivroService;

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

    @Autowired
    LivroService livroService;

    @GetMapping("/resgistro") // pagina de registro
    public String PagResgistroLivro() {
        return "ResgistrarLivro";
    }

    @PostMapping("/resgistro") // registrar livro
    public ModelAndView RegistrarLivro(Livro livro, HttpSession hSession) {

        return livroService.RegistrarLivro(livro, hSession);
    }

    @GetMapping("/estoque") // página com a cópia do repositório completo
    public ModelAndView PagListarEstoque(HttpSession hSession) {
        return livroService.PagListarEstoque(hSession);
    }

    @GetMapping("/exluir/{codigoLivro}") // botão de excluir Livro por id
    public ModelAndView ExluirLivro(@PathVariable("codigoLivro") String codigoLivro, HttpSession hSession) {
        return livroService.ExluirLivro(codigoLivro, hSession);
    }

    /*
     * @GetMapping("/alugar/{codigoLivro}") // botão de alugar por id (sem aluno) [é
     * pra debug]
     * public String AlugarLivro(@PathVariable("codigoLivro") String codigoLivro) {
     * 
     * Livro livro = repository.findById(codigoLivro).get();
     * livro.setEmprestado(true);
     * 
     * LocalDateTime date = LocalDateTime.now();
     * String livrodate = "" + date.format(dFormatter);
     * livro.setDataLivro(livrodate);
     * 
     * repository.save(livro);
     * 
     * return "redirect:/livro/estoque";
     * }
     * 
     * @GetMapping("/devolver/{codigoLivro}") // botão de devolver por id (sem
     * aluno) [é pra debug]
     * public String DevolverLivro(@PathVariable("codigoLivro") String codigoLivro)
     * {
     * 
     * Livro livro = repository.findById(codigoLivro).get(); // cria uma cópia do
     * livro com código enviado
     * 
     * if (livro.getAluno() != null) {
     * Aluno aluno = alunoRepository.findById(livro.getAluno().getId()).get();
     * aluno.setNumeroLivros(aluno.getNumeroLivros() - 1); // diminue o número de
     * livros
     * alunoRepository.save(aluno);
     * }
     * 
     * if (livro.isEmprestado()) {
     * 
     * livro.setEmprestado(false); // seta a variavel devolta pra false
     * 
     * LocalDateTime date = LocalDateTime.now(); // atualiza data e hora
     * String livrodate = "" + date.format(dFormatter);
     * livro.setDataLivro(livrodate);
     * 
     * livro.setAluno(null); // limpa a relação com aluno
     * repository.save(livro); // salva devolta no repositorio
     * 
     * LivroRecebido livroRecebido = new LivroRecebido(); // cria um livro recebido
     * com as inf do livro
     * livroRecebido.setCodigoLivro(livro.getCodigoLivro());
     * livroRecebido.setDataLivroRecebida(livro.getDataLivro());
     * livroRecebido.setNomeLivro(livro.getNomeLivro());
     * 
     * if (livro.getAluno() != null) {
     * livroRecebido.setNomeAluno(livro.getAluno().getNome());
     * livroRecebido.setIdAluno(livro.getAluno().getId());
     * }
     * 
     * livroRecebidoRepository.save(livroRecebido); // salva livro recebido num
     * repositorio diferente
     * }
     * 
     * return "redirect:/livro/estoque";
     * }
     */

    @GetMapping("/emprestados") // lista de Alugados
    public ModelAndView PagListarEmprestados(HttpSession hSession) {
        return livroService.PagListarEmprestados(hSession);
    }

    @GetMapping("reservados") // lista livros reservados
    public ModelAndView PagListarReservados(HttpSession hSession) {
        return livroService.PagListarReservados(hSession);
    }

    @GetMapping("/disponiveis") // lista de Disponíveis
    public ModelAndView PagListarDisponiveis(HttpSession hSession) {
        return livroService.PagListarDisponiveis(hSession);
    }

    @GetMapping("/devolucoes") // pagina de Log de Devoluções
    public ModelAndView PagListaLivrosDevolvidos(HttpSession hSession) {
        return livroService.PagListaLivrosDevolvidos(hSession);
    }

    @GetMapping("/devolucoes/exluirtudo") // excluir todo o log de livros devolvidos
    public String ExcluirListaDevolvidos(HttpSession hSession) {

        return livroService.ExcluirListaDevolvidos(hSession);
    }

    @GetMapping("/devolucoes/exluiritem/{ordem}") // excluir um item dos livros devolvidos
    public String ExcluirLivroDevolvidos(@PathVariable("ordem") int ordem, HttpSession hSession) {

        return livroService.ExcluirLivroDevolvidos(ordem, hSession);
    }

    @GetMapping("/editar/{codigoLivro}") // editar livros
    public String EditarLivro(@PathVariable("codigoLivro") String codigoLivro, Model model, HttpSession hSession) {
        return livroService.EditarLivro(codigoLivro, model, hSession);
    }

    // Secção pra alugar e devolver livros relacionando eles com Aluno

    @GetMapping("/alugarAluno/{codigoLivro}") // botão de alugar por id com o aluno
    public ModelAndView AlugarLivroAluno(@PathVariable("codigoLivro") String codigoLivro, HttpSession hSession) {

        return livroService.AlugarLivroAluno(codigoLivro, hSession);
    }

    @GetMapping("/devolverLivro/{codigoLivro}") // devolver livro alugados por um aluno
    public ModelAndView DevolverLivroAluno(@PathVariable("codigoLivro") String codigoLivro, HttpSession hSession) {

        return livroService.DevolverLivroAluno(codigoLivro, hSession);
    }

}
