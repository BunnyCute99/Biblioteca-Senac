package com.biblioteca.bibliotecasenac.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LivroRecebido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ordem;
    private String codigoLivro;
    private String nomeLivro;
    private String dataLivroRecebida;
    private String nomeAluno;
    private int idAluno;

    public LivroRecebido() {
    }

    public LivroRecebido(int ordem, String codigoLivro, String nomeLivro, String dataLivroRecebida, String nomeAluno,
            int idAluno) {
        this.ordem = ordem;
        this.codigoLivro = codigoLivro;
        this.nomeLivro = nomeLivro;
        this.dataLivroRecebida = dataLivroRecebida;
        this.nomeAluno = nomeAluno;
        this.idAluno = idAluno;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
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

    public String getDataLivroRecebida() {
        return dataLivroRecebida;
    }

    public void setDataLivroRecebida(String dataLivroRecebida) {
        this.dataLivroRecebida = dataLivroRecebida;
    }

    public String getNomeAluno() {
        return nomeAluno;
    }

    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }

    public int getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(int idAluno) {
        this.idAluno = idAluno;
    }

}
