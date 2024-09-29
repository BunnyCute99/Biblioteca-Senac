package com.biblioteca.bibliotecasenac.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.bibliotecasenac.Model.LivroRecebido;

@Repository
public interface LivroRecebidoRepository extends JpaRepository<LivroRecebido, Integer> {

}
