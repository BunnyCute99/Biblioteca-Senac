package com.biblioteca.bibliotecasenac.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.bibliotecasenac.Model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

}
