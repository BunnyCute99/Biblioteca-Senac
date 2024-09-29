package com.biblioteca.bibliotecasenac.Model;

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
    private boolean emprestado;

    @ManyToOne(cascade = CascadeType.MERGE)
    Aluno aluno;

    public Livro() {
    }

    public Livro(String codigoLivro, String nomeLivro, String dataLivro, boolean emprestado) {
        this.codigoLivro = codigoLivro;
        this.nomeLivro = nomeLivro;
        this.dataLivro = dataLivro;
        this.emprestado = emprestado;
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

}
