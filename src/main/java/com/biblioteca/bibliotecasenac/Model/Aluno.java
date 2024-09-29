package com.biblioteca.bibliotecasenac.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private int numeroLivros;
    private String senha;

    @OneToMany(mappedBy = "aluno")
    List<Livro> livros;

    public Aluno() {
    }

    public Aluno(int id, String nome, int numeroLivros, String senha) {
        this.id = id;
        this.nome = nome;
        this.numeroLivros = numeroLivros;
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNumeroLivros() {
        return numeroLivros;
    }

    public void setNumeroLivros(int numeroLivros) {
        this.numeroLivros = numeroLivros;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
