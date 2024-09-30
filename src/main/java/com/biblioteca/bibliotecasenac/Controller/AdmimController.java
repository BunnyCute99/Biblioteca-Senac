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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Admim;
import com.biblioteca.bibliotecasenac.Repository.AdmimRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admim")
public class AdmimController {

    @Autowired
    AdmimRepository admimRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/entrar")
    public String PagEntrarAdmim() { // Redireciona para a página de Entrar do Admim
        return "EntrarAdmin";
    }

    @PostMapping("/entrar") // Verifica se o Admim é Válido
    public ModelAndView EntrarAdmim(Admim admim, HttpSession hSession) {
        ModelAndView mv = new ModelAndView();

        // pega o Admin pelo nome no banco de dados
        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());

        // verifica se existe o admin está presente e se a senha digitada é válida
        if (aOptional.isPresent() && passwordEncoder.matches(admim.getSenha(), aOptional.get().getSenha())) {

            admim = aOptional.get(); // substitui o admin pelo o do banco de dados
            mv.setViewName("HomeAdmin"); // altera a vie para a pag. principal do admin
            mv.addObject("admim", admim); // envia o admin pra view

            hSession.setAttribute("admim", admim); // salva o admin na secção
            hSession.setMaxInactiveInterval(20 * 60); // seta o tempo de inatividade da secção em segundos
        } else { // se o admin não está presente no banco de dados ou as senha não baterem
            mv.setViewName("EntrarAdmin"); // manda devolta pra página de login
            mv.addObject("erro", "Usuário ou Senha incorretos"); // mensagem de erro
        }
        return mv;
    }

    @GetMapping("/home") // página principal do admin
    public ModelAndView PagAdmim(HttpSession hSession) {
        ModelAndView mv = new ModelAndView();
        Admim admim = (Admim) hSession.getAttribute("admim"); // pega o admin da seção

        if (admim == null) { // se não tiver nenhum admin salvo na secção
            mv.setViewName("EntrarAdmin"); // manda devolta pra tela de login
        } else { // se tiver um admin na secção
            mv.setViewName("HomeAdmin"); // manda pra secção
            mv.addObject("admim", admim); // envia o admin pra view
        }
        return mv;
    }

    @GetMapping("/registrar") // pagina pra registrar admins
    public String PagRegistrarAdmim(HttpSession hSession) {
        Admim admim = (Admim) hSession.getAttribute("admim");// pega o admin da seção

        if (admim == null) { // verifica se existe um admin da secção
            return "EntrarAdmin";
        } else {
            return "RegistarAdmim";
        }

    }

    @PostMapping("/registrar")
    public String RegistarAdmim(Admim admim, String rsenha, Model model) {

        // verifica se já existe um admin no repositório com esse nome de usuário
        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());

        // se as senhas digitadas forem iguais e não existir admin com esse nome de
        // usuário
        if (admim.getSenha().equals(rsenha) && aOptional.isEmpty()) {
            admim.setSenha(passwordEncoder.encode(admim.getSenha())); // encripta a senha
            admimRepository.save(admim); // salva o admin no repositório
        } else {
            if (aOptional.isPresent()) { // se usuário já existir
                model.addAttribute("erro", "Usuário já existe");
            } else { // se as senhas não forem iguais
                model.addAttribute("erro", "Senha não são iguais");
            }
        }
        return "RegistarAdmim";
    }

    @GetMapping("/listar") // lista admins menos o atual
    public ModelAndView ListarAdmim(HttpSession hSession) {
        ModelAndView mv = new ModelAndView();
        Admim admim = (Admim) hSession.getAttribute("admim");// pega o admin da seção

        if (admim == null) {// verifica se existe um admin da secção
            mv.setViewName("EntrarAdmin"); // se não manda devolta pra página de login
        } else {
            mv.setViewName("ListarAdmin");
            // cria a lista de admins ordenados por número de id em crescente
            List<Admim> admims = admimRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            admim = admimRepository.findById(admim.getId()).get(); // pega o admin com o id do admin da secção
            admims.remove(admim); // remove ele da lista
            mv.addObject("admims", admims); // manda a lista pra view
        }

        return mv;
    }

    @GetMapping("/editar/{id}") // editar admin por id
    public String EditarAdmim(HttpSession hSession, @PathVariable("id") int id, Model model) {

        Admim admim = (Admim) hSession.getAttribute("admim");// pega o admin da seção
        if (admim == null) {// verifica se existe um admin da secção
            return "EntrarAdmin";
        } else {
            admim = admimRepository.findById(id).get(); // pega o admin pelo id pra editar
            model.addAttribute("admim", admim);// manda pra view
            return "EditarAdimin";
        }

    }

    @PostMapping("/editar") // edita admin
    public String EditarAdmim(Admim admim, String rsenha, Model model) {

        // verifica se já existe um admin com esse nome de usuário
        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());

        if (aOptional.get().getId() == admim.getId()) { // se o admin editado tem o mesmo id do admin do repositório
            aOptional = Optional.empty(); // esvazia o Optional
        }

        // se as senha digitadas são iguais e Optional está vazio, seja porque não
        // existe um admin com o mesmo nome de usuário ou porque o nome de usuário é o
        // mesmo do admin que está sendo editado.
        if (admim.getSenha().equals(rsenha) && aOptional.isEmpty()) {
            admim.setSenha(passwordEncoder.encode(admim.getSenha())); // encripta a senha
            admimRepository.save(admim); // salva as alterações no banco de dados
            return "redirect:/admim/listar"; // redireciona pra listar os admins
        } else {
            model.addAttribute("admim", admim); // manda o admin pra view
            model.addAttribute("erro", "Senha Não São Iguais ou Usuário já Existe"); // mensagem de erro
            return "EditarAdimin";
        }
    }

    @GetMapping("/excluir/{id}") // excluir admin por id
    public String ExcluirAdmim(HttpSession hSession, @PathVariable("id") int id) {
        Admim admim = (Admim) hSession.getAttribute("admim"); // pega o admin da seção

        if (admim == null) { // verifica se existe um admin da secção
            return "EntrarAdmin";
        } else {
            admimRepository.deleteById(id); // deleta o admin pelo id
            return "redirect:/admim/listar";
        }
    }

}
