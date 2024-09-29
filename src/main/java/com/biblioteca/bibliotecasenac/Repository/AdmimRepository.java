package com.biblioteca.bibliotecasenac.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.bibliotecasenac.Model.Admim;
import java.util.Optional;

@Repository
public interface AdmimRepository extends JpaRepository<Admim, Integer> {

    Optional<Admim> findByUsuario(String usuario);
}
