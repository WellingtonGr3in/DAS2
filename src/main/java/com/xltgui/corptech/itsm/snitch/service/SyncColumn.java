package com.xltgui.corptech.itsm.snitch.service;

public class SyncColumn {
    private String nome;        // nome da coluna no destino
    private String nomeFonte;   // nome da coluna na fonte (pode ser diferente)
    private Class<?> tipoJava;  // tipo Java da coluna
    
    public SyncColumn(String nome, String nomeFonte, Class<?> tipoJava) {
        this.nome = nome;
        this.nomeFonte = nomeFonte;
        this.tipoJava = tipoJava;
    }
    
    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getNomeFonte() { return nomeFonte; }
    public void setNomeFonte(String nomeFonte) { this.nomeFonte = nomeFonte; }
    
    public Class<?> getTipoJava() { return tipoJava; }
    public void setTipoJava(Class<?> tipoJava) { this.tipoJava = tipoJava; }
}