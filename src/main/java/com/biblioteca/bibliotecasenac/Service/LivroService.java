package com.biblioteca.bibliotecasenac.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Aluno;
import com.biblioteca.bibliotecasenac.Model.Livro;
import com.biblioteca.bibliotecasenac.Model.LivroRecebido;
import com.biblioteca.bibliotecasenac.Repository.AlunoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRecebidoRepository;
import com.biblioteca.bibliotecasenac.Repository.LivroRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class LivroService {

    @Autowired
    LivroRepository livroRepository;
    @Autowired
    LivroRecebidoRepository livroRecebidoRepository;
    @Autowired
    AlunoRepository alunoRepository;
    @Autowired
    AlunoService alunoService;
    @Autowired
    AdminService adminService;

    // formato da data em dias/mes/ano, horas:minutos:segundos, dia da semana
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss, EEEE");

    // registar um livro
    public ModelAndView RegistrarLivro(Livro livro, HttpSession hSession) {

        Optional<Livro> lOptional = livroRepository.findById(livro.getCodigoLivro());

        // página pra onde será enviado
        ModelAndView mv = new ModelAndView("ResgistrarLivro");

        // autentifica um admin
        if (adminService.AutentificarAdmim(hSession)) {

            // verifica se há um livro no Optional, se tiver significa que já existem um
            // livro no repositório com esse código
            if (lOptional.isPresent()) {
                // mensagem de erro
                mv.addObject("erro", "Código de livro já existe");

            } else {
                // valor padrão de emprestado
                livro.setEmprestado(false);
                // valor padrão de reservado
                livro.setReservado(false);
                // seta o codigo pra letras maiusculas
                livro.setCodigoLivro(livro.getCodigoLivro().toUpperCase());
                // seta o título pra letras maiusculas
                livro.setNomeLivro(livro.getNomeLivro().toUpperCase());
                // pega a hora atual do computador, então converte em um String com a formatação
                LocalDateTime date = LocalDateTime.now();
                String livrodate = "" + date.format(dateTimeFormatter);
                // seta a data no livro
                livro.setDataLivro(livrodate);
                // salva livro no repositório
                livroRepository.save(livro);
                // envia uma mensagem de certo pra view
                mv.addObject("certo", "Livro registrado com sucesso");

            }

        } else {
            mv.setViewName("EntrarAdmin");
        }

        return mv;
    }

    // página com todo o repositório de livros
    public ModelAndView PagListarEstoque(HttpSession hSession) {
        // página destino
        ModelAndView mv = new ModelAndView("EstoqueLivros");
        // lista de livros ordenados em ordem ascndente do título do livro
        List<Livro> livros = livroRepository.findAll(Sort.by(Sort.Direction.ASC, "nomeLivro"));
        // mannda a lista pra view
        mv.addObject("livros", livros);
        // adicionar opcoes de Pesquisa
        List<String> opcoes = new ArrayList<>();
        opcoes.add("TÍTULO");
        opcoes.add("CÓDIGO");

        mv.addObject("opcoes", opcoes);
        // autentificação de admim
        return adminService.AutentificarAdmim(hSession, mv);
    }

    // excluir livro
    public ModelAndView ExcluirLivro(String codigoLivro, HttpSession hSession) {
        ModelAndView mv = new ModelAndView("redirect:/livro/estoque");

        if (adminService.AutentificarAdmim(hSession)) {

            Livro livro = livroRepository.findById(codigoLivro).get();
            if (livro.getAluno() != null) { // se o livro ter uma associação com algum aluno
                Aluno aluno = alunoRepository.findById(livro.getAluno().getId()).get();
                aluno.setNumeroLivros(aluno.getNumeroLivros() - 1);
                alunoRepository.save(aluno);
            }

            livroRepository.deleteById(codigoLivro);

        } else {
            mv.setViewName("EntrarAdmin");
        }

        return mv;
    }

    // lista os livros que foram emprestado aos usuários
    public ModelAndView PagListarEmprestados(HttpSession hSession) {
        ModelAndView mv = new ModelAndView("LivrosAlugados");

        List<Livro> livrosAlugados = livroRepository.findByEmprestado(true); // acha livros que estejam emprestados
        mv.addObject("livros", livrosAlugados);
        return adminService.AutentificarAdmim(hSession, mv);
    }

    // lista livros reservados por usuários
    public ModelAndView PagListarReservados(HttpSession hSession) {
        ModelAndView mv = new ModelAndView("LivrosReservados");

        // acha livros que estão reservados
        List<Livro> livros = livroRepository.findByReservado(true);
        mv.addObject("livros", livros);

        return adminService.AutentificarAdmim(hSession, mv);
    }

    // lista livros que estão disponíveis na biblioteca
    public ModelAndView PagListarDisponiveis(HttpSession hSession) {
        ModelAndView mv = new ModelAndView("LivrosDisponiveis");

        // acha livros ques não estam emprestados
        List<Livro> livrosDisponiveis = livroRepository.findByEmprestadoAndReservado(false, false);
        // manda a lista praa view
        mv.addObject("livros", livrosDisponiveis);
        return adminService.AutentificarAdmim(hSession, mv);
    }

    // página de log de livros devolvidos
    public ModelAndView PagListaLivrosDevolvidos(HttpSession hSession) {
        ModelAndView mv = new ModelAndView("LogLivrosRecebidos");
        // lista com o log dos livros deolvidos
        List<LivroRecebido> lRecebidos = livroRecebidoRepository.findAll();
        // envia lista pra view
        mv.addObject("livros", lRecebidos);

        return adminService.AutentificarAdmim(hSession, mv);
    }

    // exclui todo o log de livros devolvidos
    public String ExcluirListaDevolvidos(HttpSession hSession) {

        if (adminService.AutentificarAdmim(hSession)) {
            livroRecebidoRepository.deleteAll();
            return "redirect:/livro/devolucoes";
        } else {
            return "EntrarAdmin";
        }

    }

    // exclui 1 item do log de livros devolvidos
    public String ExcluirLivroDevolvidos(int ordem, HttpSession hSession) {

        if (adminService.AutentificarAdmim(hSession)) {
            livroRecebidoRepository.deleteById(ordem);
            return "redirect:/livro/devolucoes";
        } else {
            return "EntrarAdmin";
        }

    }

    // edita livro
    public String EditarLivro(String codigoLivro, Model model, HttpSession hSession) {

        if (adminService.AutentificarAdmim(hSession)) {
            Livro livro = livroRepository.findById(codigoLivro).get();
            model.addAttribute("livro", livro);
            return "EditarLivro";
        } else {
            return "EntrarAdmin";
        }

    }

    public ModelAndView PostEditarLivro(Livro livro, HttpSession hSession) {

        Optional<Livro> lOptional = livroRepository.findById(livro.getCodigoLivro());

        if (lOptional.isPresent()) {
            lOptional.get().setNomeLivro(livro.getNomeLivro());
            lOptional.get().setDataLivro(AtualizarHoraAgora());

            livroRepository.save(lOptional.get());
        }

        return PagListarEstoque(hSession);
    }

    // Atualizar hora
    public String AtualizarHoraAgora() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return "" + localDateTime.format(dateTimeFormatter);
    }

    // Adiciona a secção as Lista de Livros Disponíveis e de Alugados pelo Aluno
    public ModelAndView AdicionaDevolvidosDisponiveis(ModelAndView mv, Aluno aluno) {

        // lista de livros disponiveis
        List<Livro> livrosDisponiveis = livroRepository.findByEmprestadoAndReservado(false, false);

        // lista de livros alugados pelo aluno
        List<Livro> livrosAlugados = livroRepository.findByEmprestadoAndAluno(true, aluno);

        List<String> opcoes = new ArrayList<>();
        opcoes.add("TÍTULO");
        opcoes.add("CÓDIGO");

        mv.addObject("aluno", aluno);
        mv.addObject("livros", livrosDisponiveis);
        mv.addObject("livrosAlugados", livrosAlugados);
        mv.addObject("opcoes", opcoes);

        return mv;
    }

    // Adiciona a secção as Lista de Livros Disponíveis e de Alugados pelo Aluno
    public ModelAndView AdicionaDevolvidosDisponiveisReserva(ModelAndView mv, Aluno aluno) {

        // lista de livros disponiveis
        List<Livro> livrosDisponiveis = livroRepository.findByEmprestadoAndReservado(false, false);

        // lista de livros reservados pelo aluno
        List<Livro> livrosReservados = livroRepository.findByReservadoAndAluno(true, aluno);

        List<String> opcoes = new ArrayList<>();
        opcoes.add("TÍTULO");
        opcoes.add("CÓDIGO");

        mv.addObject("aluno", aluno);
        mv.addObject("livros", livrosDisponiveis);
        mv.addObject("livrosReservados", livrosReservados);
        mv.addObject("opcoes", opcoes);

        return mv;
    }

    // Adiciona uma entrada no log de livros
    public void EntrdaLogLivros(Livro livro) {

        LivroRecebido livroRecebido = new LivroRecebido(); // entrada do log de livro
        livroRecebido.setCodigoLivro(livro.getCodigoLivro());
        livroRecebido.setDataLivroRecebida(livro.getDataLivro());
        livroRecebido.setNomeLivro(livro.getNomeLivro());
        livroRecebido.setNomeAluno(livro.getAluno().getNome());
        livroRecebido.setIdAluno(livro.getAluno().getId());

        livroRecebidoRepository.save(livroRecebido); // salva emtrada do log

    }

    // Secção pra alugar e devolver livros relacionando eles com Aluno

    // Alugar livro
    public ModelAndView AlugarLivroAluno(String codigoLivro, HttpSession hSession) {

        ModelAndView mv = new ModelAndView();

        if (alunoService.AutentificarAluno(hSession)) {
            mv.setViewName("AlugarLivros");
            Livro livro = livroRepository.findById(codigoLivro).get();
            Aluno aluno = (Aluno) hSession.getAttribute("aluno");

            // se o livro não está emprestado e o aluno possuí menos de 5 livro alugados
            if (aluno.getNumeroLivros() < 5 && livro.isEmprestado() == false) {

                livro.setEmprestado(true);
                livro.setAluno(alunoRepository.findById(aluno.getId()).get());
                // atualiza a data do livro pra data atual
                livro.setDataLivro(AtualizarHoraAgora());
                // salva livro no Banco
                livroRepository.save(livro);
                // atualiza o aluno no Banco e na Secção
                aluno.setNumeroLivros(aluno.getNumeroLivros() + 1);
                alunoRepository.save(aluno);
                hSession.setAttribute("aluno", aluno);
            } else {
                // mensagem de erro
                mv.addObject("erro", "limite livros atingido ou livro já está alugado");
            }
            // adiciona as listas (Disponíveis e Alugados pelo Aluno) para view
            mv = AdicionaDevolvidosDisponiveis(mv, aluno);

        } else {
            mv.setViewName("EntrarAluno");
        }
        return mv;
    }

    public ModelAndView reservarLivroAluno(String codigoLivro, HttpSession hSession) {

        ModelAndView mv = new ModelAndView();

        if (alunoService.AutentificarAluno(hSession)) {
            mv.setViewName("ReservarLivros");
            Livro livro = livroRepository.findById(codigoLivro).get();
            Aluno aluno = (Aluno) hSession.getAttribute("aluno");

            // se o livro não está reservado e o aluno possui menos de 5 livro alugados
            if (aluno.getNumeroLivros() < 5 && livro.isReservado() == false && livro.isEmprestado() == false) {

                livro.setReservado(true);

                // "agora" pega a data atual para que "limite" adicione +2 no dia
                LocalDateTime agora = LocalDateTime.now();
                LocalDateTime limite = agora.plusDays(2);
                String limiteString = "" + limite.format(dateTimeFormatter);
                // seta a data limite
                livro.setLimiteReserva(limite);
                // seta aluno
                livro.setAluno(alunoRepository.findById(aluno.getId()).get());
                // atualiza a data do livro pra data atual
                livro.setDataLivro(AtualizarHoraAgora());
                // data limite para a retirada do livro
                livro.setDataLimiteReserva(limiteString);
                // salva livro no Banco
                livroRepository.save(livro);
                // atualiza o aluno no Banco e na Secção
                aluno.setNumeroLivros(aluno.getNumeroLivros() + 1);
                alunoRepository.save(aluno);
                hSession.setAttribute("aluno", aluno);
            } else {
                // mensagem de erro
                mv.addObject("erro", "limite livros atingido ou livro já está alugado/reservado");
            }
            // adiciona as listas (Disponíveis e Reservados pelo Aluno) para view
            mv = AdicionaDevolvidosDisponiveisReserva(mv, aluno);

        } else {
            mv.setViewName("EntrarAluno");
        }
        return mv;
    }

    // devolver livro
    public ModelAndView DevolverLivroAluno(String codigoLivro, HttpSession hSession) {
        ModelAndView mv = new ModelAndView();
        if (alunoService.AutentificarAluno(hSession)) {

            mv.setViewName("AlugarLivros");
            Livro livro = livroRepository.findById(codigoLivro).get();
            Aluno aluno = (Aluno) hSession.getAttribute("aluno");

            if (livro.isEmprestado()) {
                // deolve o livro pra biblioteca
                livro.setEmprestado(false);
                // atualiza a data
                livro.setDataLivro(AtualizarHoraAgora());
                // cria uma entrada de log baseada no livro
                EntrdaLogLivros(livro);

                livro.setAluno(null); // limpa a relação com aluno do livro
                livroRepository.save(livro);

                // atualiza o aluno no Banco e na Secção
                aluno.setNumeroLivros(aluno.getNumeroLivros() - 1);
                alunoRepository.save(aluno);
                hSession.setAttribute("aluno", aluno);

            } else {
                mv.addObject("erro", "livro já está devolvido");
            }

            // adiciona as listas (Disponíveis e Alugados pelo Aluno) para view
            mv = AdicionaDevolvidosDisponiveis(mv, aluno);
        } else {
            mv.setViewName("EntrarAluno");
        }
        return mv;
    }

    // cancelar reserva livro
    public ModelAndView CancelarLivroAluno(String codigoLivro, HttpSession hSession) {
        ModelAndView mv = new ModelAndView();
        if (alunoService.AutentificarAluno(hSession)) {

            mv.setViewName("ReservarLivros");
            Livro livro = livroRepository.findById(codigoLivro).get();
            Aluno aluno = (Aluno) hSession.getAttribute("aluno");

            if (livro.isReservado()) {
                // devolve o livro pra biblioteca
                livro.setReservado(false);
                // atualiza a data
                livro.setDataLivro(AtualizarHoraAgora());
                // limpa a data limite
                livro.setLimiteReserva(null);
                // limpa a string da data
                livro.setDataLimiteReserva(null);
                // *adicionar um log de devoluções canceladas?

                livro.setAluno(null); // limpa a relação com aluno do livro
                livroRepository.save(livro);

                // atualiza o aluno no Banco e na Secção
                aluno.setNumeroLivros(aluno.getNumeroLivros() - 1);
                alunoRepository.save(aluno);
                hSession.setAttribute("aluno", aluno);

            } else {
                mv.addObject("erro", "livro já está devolvido");
            }

            // adiciona as listas (Disponíveis e Alugados pelo Aluno) para view
            mv = AdicionaDevolvidosDisponiveisReserva(mv, aluno);
        } else {
            mv.setViewName("EntrarAluno");
        }
        return mv;
    }

    // Busca de Livro

    public List<Livro> BuscarLivrosTipoString(String buscaLivro, String tipoBusca) {
        List<Livro> livros = new ArrayList<>();
        if (tipoBusca.equals("TÍTULO")) {
            livros = livroRepository.findByNomeLivroContainingIgnoreCase(buscaLivro);
        }
        if (tipoBusca.equals("CÓDIGO")) {
            livros = livroRepository.findByCodigoLivroContainingIgnoreCase(buscaLivro);
        }
        return livros;
    }

    // para o Estoque de Livros
    public ModelAndView BuscarLivros(HttpSession hSession, String buscaLivro, String tipoBusca) {

        List<String> opcoes = new ArrayList<>();
        opcoes.add("TÍTULO");
        opcoes.add("CÓDIGO");

        ModelAndView mv = new ModelAndView("EstoqueLivros");
        if (buscaLivro != null) {
            List<Livro> livros = BuscarLivrosTipoString(buscaLivro, tipoBusca);
            mv.addObject("livros", livros);
            mv.addObject("tipoBusca", tipoBusca);
            mv.addObject("buscaLivro", buscaLivro);
            mv.addObject("opcoes", opcoes);

        }

        return adminService.AutentificarAdmim(hSession, mv);

    }

}
