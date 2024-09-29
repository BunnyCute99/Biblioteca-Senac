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
    public String PagEntrarAdmim() { // Redireciona para a página de Entrar com id do Aluno
        return "EntrarAdmin";
    }

    @PostMapping("/entrar")
    public ModelAndView EntrarAdmim(Admim admim, HttpSession hSession) {
        ModelAndView mv = new ModelAndView();

        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());

        if (aOptional.isPresent() && passwordEncoder.matches(admim.getSenha(), aOptional.get().getSenha())) {
            admim = aOptional.get();
            mv.setViewName("HomeAdmin");
            mv.addObject("admim", admim);
            hSession.setAttribute("admim", admim);
            hSession.setMaxInactiveInterval(20 * 60);
        } else {
            mv.setViewName("EntrarAdmin");
            mv.addObject("erro", "Usuário ou Senha incorretos");
        }
        return mv;
    }

    @GetMapping("/home")
    public ModelAndView PagAdmim(HttpSession hSession) {
        ModelAndView mv = new ModelAndView();
        Admim admim = (Admim) hSession.getAttribute("admim");

        if (admim == null) {
            mv.setViewName("EntrarAdmin");
        } else {
            mv.setViewName("HomeAdmin");
            mv.addObject("admim", admim);
        }
        return mv;
    }

    @GetMapping("/registrar")
    public String PagRegistrarAdmim(HttpSession hSession) {
        Admim admim = (Admim) hSession.getAttribute("admim");

        if (admim == null) {
            return "EntrarAdmin";
        } else {
            return "RegistarAdmim";
        }

    }

    @PostMapping("/registrar")
    public String RegistarAdmim(Admim admim, String rsenha, Model model) {

        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());

        if (admim.getSenha().equals(rsenha) && aOptional.isEmpty()) {
            admim.setSenha(passwordEncoder.encode(admim.getSenha()));
            admimRepository.save(admim);
        } else {
            if (aOptional.isEmpty()) {
                model.addAttribute("erro", "Senha não são iguais");
            } else {
                model.addAttribute("erro", "Usuário já existe");
            }
        }
        return "RegistarAdmim";
    }

    @GetMapping("/listar")
    public ModelAndView ListarAdmim(HttpSession hSession) {
        ModelAndView mv = new ModelAndView();
        Admim admim = (Admim) hSession.getAttribute("admim");

        if (admim == null) {
            mv.setViewName("EntrarAdmin");
        } else {
            mv.setViewName("ListarAdmin");
            List<Admim> admims = admimRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            admim = admimRepository.findById(admim.getId()).get();
            admims.remove(admim);
            mv.addObject("admims", admims);
        }

        return mv;
    }

    @GetMapping("/editar/{id}")
    public String EditarAdmim(HttpSession hSession, @PathVariable("id") int id, Model model) {

        Admim admim = (Admim) hSession.getAttribute("admim");
        if (admim == null) {
            return "EntrarAdmin";
        } else {
            admim = admimRepository.findById(id).get();
            model.addAttribute("admim", admim);
            return "EditarAdimin";
        }

    }

    @PostMapping("/editar")
    public String EditarAdmim(Admim admim, String rsenha, Model model) {

        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());
        if (aOptional.get().getId() == admim.getId()) {
            aOptional = Optional.empty();
        }

        if (admim.getSenha().equals(rsenha) && aOptional.isEmpty()) {
            admim.setSenha(passwordEncoder.encode(admim.getSenha()));
            admimRepository.save(admim);
            return "redirect:/admim/listar";
        } else {
            model.addAttribute("admim", admim);
            model.addAttribute("erro", "Senha Não São Iguais ou Usuário já Existe");
            return "EditarAdimin";
        }
    }

    @GetMapping("/excluir/{id}")
    public String ExcluirAdmim(HttpSession hSession, @PathVariable("id") int id) {
        Admim admim = (Admim) hSession.getAttribute("admim");

        if (admim == null) {
            return "EntrarAdmin";
        } else {
            admimRepository.deleteById(id);
            return "redirect:/admim/listar";
        }
    }

}
