package com.biblioteca.bibliotecasenac.Model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Livro {

    @Id
    private String codigoLivro;
    private String nomeLivro;
    private String dataLivro;
    private String dataLimiteReserva;
    private boolean emprestado;
    private boolean reservado;
    private LocalDateTime limiteReserva;

    @ManyToOne(cascade = CascadeType.MERGE)
    Aluno aluno;

    public Livro() {
    }

    public Livro(String codigoLivro, String nomeLivro, String dataLivro, String dataLimiteReserva, boolean emprestado,
            boolean reservado, LocalDateTime limiteReserva, Aluno aluno) {
        this.codigoLivro = codigoLivro;
        this.nomeLivro = nomeLivro;
        this.dataLivro = dataLivro;
        this.dataLimiteReserva = dataLimiteReserva;
        this.emprestado = emprestado;
        this.reservado = reservado;
        this.limiteReserva = limiteReserva;
        this.aluno = aluno;
    }

    public String getCodigoLivro() {
        return codigoLivro;
    }

    public void setCodigoLivro(String codigoLivro) {
        this.codigoLivro = codigoLivro;
    }

    public String getNomeLivro() {
        return nomeLivro;
    }

    public void setNomeLivro(String nomeLivro) {
        this.nomeLivro = nomeLivro;
    }

    public String getDataLivro() {
        return dataLivro;
    }

    public void setDataLivro(String dataLivro) {
        this.dataLivro = dataLivro;
    }

    public String getDataLimiteReserva() {
        return dataLimiteReserva;
    }

    public void setDataLimiteReserva(String dataLimiteReserva) {
        this.dataLimiteReserva = dataLimiteReserva;
    }

    public boolean isEmprestado() {
        return emprestado;
    }

    public void setEmprestado(boolean emprestado) {
        this.emprestado = emprestado;
    }

    public boolean isReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }

    public LocalDateTime getLimiteReserva() {
        return limiteReserva;
    }

    public void setLimiteReserva(LocalDateTime limiteReserva) {
        this.limiteReserva = limiteReserva;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

}
