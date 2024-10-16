package com.biblioteca.bibliotecasenac.Model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Livro {

    @Id
    private String codigoLivro;
    private String nomeLivro;
    private String dataLivro;
    private boolean emprestado;
    private boolean reservado;
    private LocalDateTime dataLimiteReserva;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;

    @ManyToOne(cascade = CascadeType.MERGE)
    Aluno aluno;

    public Livro() {
    }

    public Livro(String codigoLivro, String nomeLivro, String dataLivro, boolean emprestado, boolean reservado,
            LocalDateTime dataLimiteReserva, LocalDateTime dataEntrada, LocalDateTime dataSaida, Aluno aluno) {
        this.codigoLivro = codigoLivro;
        this.nomeLivro = nomeLivro;
        this.dataLivro = dataLivro;
        this.emprestado = emprestado;
        this.reservado = reservado;
        this.dataLimiteReserva = dataLimiteReserva;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
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

    public boolean isEmprestado() {
        return emprestado;
    }

    public void setEmprestado(boolean emprestado) {
        this.emprestado = emprestado;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public boolean isReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }

    public LocalDateTime getDataLimiteReserva() {
        return dataLimiteReserva;
    }

    public void setDataLimiteReserva(LocalDateTime dataLimiteReserva) {
        this.dataLimiteReserva = dataLimiteReserva;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

}
