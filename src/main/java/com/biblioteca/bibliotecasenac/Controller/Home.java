package com.biblioteca.bibliotecasenac.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Aluno;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class Home {

    @GetMapping("/home")
    public String PagHome() {
        return "Home";
    }

    @GetMapping("/home¬aluno")
    public ModelAndView PagHomeAluno(HttpSession hSession) {
        ModelAndView mv = new ModelAndView();
        Aluno aluno = (Aluno) hSession.getAttribute("aluno");
        if (aluno == null) {
            mv.setViewName("EntrarAluno");

        } else {
            mv.setViewName("HomeAluno");
            mv.addObject("aluno", aluno);
        }
        return mv;

    }

    @GetMapping("/home¬biblioteca")
    public String PagHomeBiblioteca() {
        return "HomeBiblioteca";
    }

}
