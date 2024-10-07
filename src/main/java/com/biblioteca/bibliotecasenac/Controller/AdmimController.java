package com.biblioteca.bibliotecasenac.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Admim;
import com.biblioteca.bibliotecasenac.Repository.AdmimRepository;
import com.biblioteca.bibliotecasenac.Service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admim")
public class AdmimController {

    @Autowired
    AdmimRepository admimRepository;
    @Autowired
    AdminService adminService;

    @GetMapping("/padrao")
    public String Setar() {
        adminService.SetarPadrão();
        return "Home";
    }

    @GetMapping("/entrar")
    public String PagEntrarAdmim() { // Redireciona para a página de Entrar do Admim
        return "EntrarAdmin";
    }

    @PostMapping("/entrar") // Verifica se o Admim é Válido
    public ModelAndView EntrarAdmim(Admim admim, HttpSession hSession) {
        return adminService.EntrarAdmim(admim, hSession);
    }

    @GetMapping("/home") // página principal do admin
    public ModelAndView PagAdmim(HttpSession hSession) {
        return adminService.PagAdmim(hSession);
    }

    @GetMapping("/registrar") // pagina pra registrar admins
    public ModelAndView PagRegistrarAdmim(HttpSession hSession) {
        return adminService.PagRegistrarAdmim(hSession);
    }

    @PostMapping("/registrar")
    public ModelAndView RegistarAdmim(Admim admim, String rsenha, HttpSession hSession) {

        return adminService.RegistarAdmim(admim, rsenha, hSession);
    }

    @GetMapping("/listar") // lista admins menos o atual
    public ModelAndView ListarAdmim(HttpSession hSession) {
        return adminService.ListarAdmim(hSession);
    }

    @GetMapping("/editar/{id}") // get a página de editar
    public ModelAndView EditarAdmim(HttpSession hSession, @PathVariable("id") int id) {
        return adminService.EditarAdmim(hSession, id);
    }

    @PostMapping("/editar") // edita admin
    public ModelAndView EditarAdmim(Admim admim, String rsenha, HttpSession hSession) {
        return adminService.EditarAdmim(admim, rsenha, hSession);
    }

    @GetMapping("/excluir/{id}") // excluir admin por id
    public ModelAndView ExcluirAdmim(HttpSession hSession, @PathVariable("id") int id) {
        return adminService.ExcluirAdmim(hSession, id);
    }

}
