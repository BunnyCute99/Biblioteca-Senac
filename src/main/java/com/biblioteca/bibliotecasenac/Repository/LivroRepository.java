package com.biblioteca.bibliotecasenac.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.bibliotecasenac.Model.Livro;
import com.biblioteca.bibliotecasenac.Model.Aluno;

@Repository
public interface LivroRepository extends JpaRepository<Livro, String> {

    List<Livro> findByEmprestado(boolean emprestado);

    List<Livro> findByEmprestadoAndReservado(boolean emprestado, boolean reservado);

    List<Livro> findByEmprestadoAndAluno(boolean emprestado, Aluno aluno);

    List<Livro> findByReservadoAndAluno(boolean reservado, Aluno aluno);

    List<Livro> findByReservado(boolean reservado);

    List<Livro> findByNomeLivroContainingIgnoreCase(String nomeLivro);

    List<Livro> findByCodigoLivroContainingIgnoreCase(String codigoLivro);

    List<Livro> findByNomeLivroContainingIgnoreCaseAndEmprestadoAndReservado(String nomeLivro, boolean emprestado,
            boolean reservado);

    List<Livro> findByCodigoLivroContainsIgnoreCaseAndEmprestadoAndReservado(String codigoLivro, boolean emprestado,
            boolean reservado);

}
