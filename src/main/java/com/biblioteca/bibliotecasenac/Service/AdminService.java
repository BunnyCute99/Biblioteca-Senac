package com.biblioteca.bibliotecasenac.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.bibliotecasenac.Model.Admim;
import com.biblioteca.bibliotecasenac.Repository.AdmimRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminService {

    @Autowired
    AdmimRepository admimRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // cria ou muda o admin 1, [pra usar caso não aja admins no repositório]
    public void SetarPadrão() {
        Admim admim = new Admim();
        admim.setId(1);
        admim.setNome("Administrador");
        admim.setUsuario("admin");
        admim.setSenha("123");
        admim.setSenha(passwordEncoder.encode(admim.getSenha()));
        admimRepository.save(admim);
    }

    // página de login de Admin
    public ModelAndView EntrarAdmim(Admim admim, HttpSession hSession) {
        ModelAndView mv = new ModelAndView();

        // pega o Admin pelo nome de usuário no banco de dados
        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());

        // verifica se existe o admin está presente e se a senha digitada é válida
        if (aOptional.isPresent() && passwordEncoder.matches(admim.getSenha(), aOptional.get().getSenha())) {

            admim = aOptional.get(); // substitui o admin pelo o do banco de dados
            mv.setViewName("HomeAdmin"); // altera a view para a pag. principal do admin
            mv.addObject("admim", admim); // envia o admin pra view

            hSession.setAttribute("admim", admim); // salva o admin na secção
            hSession.setMaxInactiveInterval(-1); // seta o tempo de inatividade da secção em segundos

        } else { // se o admin não está presente no banco de dados ou as senha não baterem
            mv.setViewName("EntrarAdmin"); // manda devolta pra página de login
            mv.addObject("erro", "Usuário ou Senha incorretos"); // mensagem de erro
        }
        return mv;
    }

    // autentificar, ele recebe como parámetro a secção e a view pra onde deverá ser
    // levado se a autentificação estiver certo
    public ModelAndView AutentificarAdmim(HttpSession hSession, ModelAndView mv) {
        // procura na secção por um atributo "admim" e joga na variável
        Admim admim = (Admim) hSession.getAttribute("admim");
        if (admim == null) { // se a variável estiver nula significa que não há nenhum admim na secção
            mv.setViewName("EntrarAdmin"); // então manda pra tela de login
        }
        return mv; // manda a view devolta
    }

    public boolean AutentificarAdmim(HttpSession hSession) {
        Admim admim = (Admim) hSession.getAttribute("admim");
        if (admim == null) {
            return false;
        } else {
            return true;
        }

    }

    public ModelAndView PagAdmim(HttpSession hSession) { // get na pagina principal de admin

        Admim admim = (Admim) hSession.getAttribute("admim");

        ModelAndView mv = new ModelAndView("HomeAdmin"); // página pra onde será enviado
        mv.addObject("admim", admim); // adiciona admim na view

        return AutentificarAdmim(hSession, mv); // metodo de autentificar
    }

    public ModelAndView PagRegistrarAdmim(HttpSession hSession) { // get da página de registro de admin

        ModelAndView mv = new ModelAndView("RegistarAdmim"); // pagina onde se deseja enviar
        return AutentificarAdmim(hSession, mv); // metodo de autentificar

    }

    public ModelAndView RegistarAdmim(Admim admim, String rsenha, HttpSession hSession) {

        // verifica se já existe um admin no repositório com esse nome de usuário
        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());

        // página desejada
        ModelAndView mv = new ModelAndView("RegistarAdmim");

        if (AutentificarAdmim(hSession)) {
            // se as senhas digitadas forem iguais e não existir admin com esse nome de
            // usuário
            if (admim.getSenha().equals(rsenha) && aOptional.isEmpty()) {
                admim.setSenha(passwordEncoder.encode(admim.getSenha())); // encripta a senha
                admimRepository.save(admim); // salva o admin no repositório
                mv.addObject("certo", "Administrador registrado com sucesso");
            } else {
                if (aOptional.isPresent()) { // se usuário já existir
                    mv.addObject("erro", "ERRO: Usuário já existe");
                } else { // se as senhas não forem iguais
                    mv.addObject("erro", "ERRO: Senhas não são iguais");
                }
            }

        } else {
            mv.setViewName("EntrarAdmin");
        }

        return mv;
    }

    public ModelAndView ListarAdmim(HttpSession hSession) {
        ModelAndView mv = new ModelAndView("ListarAdmin");

        // cria a lista de admins ordenados por número de id em crescente
        List<Admim> admims = admimRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        // remove o admin atual da lista pra evitar que ele possa deletar a si mesmo
        Admim admim = (Admim) hSession.getAttribute("admim");
        admim = admimRepository.findById(admim.getId()).get();
        admims.remove(admim);
        mv.addObject("admims", admims); // manda a lista pra view

        return AutentificarAdmim(hSession, mv);
    }

    public ModelAndView EditarAdmim(HttpSession hSession, int id) {

        ModelAndView mv = new ModelAndView("EditarAdimin");

        if (AutentificarAdmim(hSession)) {

            Admim admim = admimRepository.findById(id).get(); // pega o admin pelo id pra editar
            mv.addObject("admim", admim);// manda pra view

        } else {
            mv.setViewName("EntrarAdmin");
        }

        return mv;

    }

    public ModelAndView EditarAdmim(Admim admim, String rsenha, HttpSession hSession) {

        // verifica se já existe um admin com esse nome de usuário
        Optional<Admim> aOptional = admimRepository.findByUsuario(admim.getUsuario());

        ModelAndView mv = new ModelAndView();

        if (AutentificarAdmim(hSession)) {

            if (aOptional.isPresent()) {
                if (aOptional.get().getId() == admim.getId()) { // se o admin editado tem o mesmo id do admin do
                                                                // repositório
                    aOptional = Optional.empty(); // esvazia o Optional
                }

            }

            // se as senha digitadas são iguais e Optional está vazio, seja porque não
            // existe um admin com o mesmo nome de usuário ou porque o nome de usuário é o
            // mesmo do admin que está sendo editado.
            if (admim.getSenha().equals(rsenha) && aOptional.isEmpty()) {
                admim.setSenha(passwordEncoder.encode(admim.getSenha())); // encripta a senha
                admimRepository.save(admim); // salva as alterações no banco de dados
                mv.setViewName("redirect:/admim/listar"); // redireciona pra listar os admins
            } else {
                mv.addObject("admim", admim); // manda o admin pra view
                mv.addObject("erro", "Senhas NÃO são iguais ou usuário já existe"); // mensagem de erro
                mv.setViewName("EditarAdimin");
            }

        } else {
            mv.setViewName("EntrarAdmin");
        }

        return mv;
    }

    public ModelAndView ExcluirAdmim(HttpSession hSession, int id) {

        ModelAndView mv = new ModelAndView("redirect:/admim/listar");

        if (AutentificarAdmim(hSession)) {
            admimRepository.deleteById(id); // deleta o admin pelo id
        } else {
            mv.setViewName("EntrarAdmin");
        }

        return mv;
    }

}
