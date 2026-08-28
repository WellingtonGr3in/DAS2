package com.xltgui.corptech.itsm.snitch.service;

import java.util.List;

public class SyncConfig {
    private String nomeTabela;
    private String schemaFonte = "istm";  // default
    private String schemaDestino = "istm"; // default
    private String colunaId;  // nome da coluna ID na fonte
    private String colunaIdFonte;  // nome da coluna que guarda o ID fonte no destino
    private List<SyncColumn> colunas;
    
    // Construtores
    public SyncConfig() {}
    
    public SyncConfig(String nomeTabela, String colunaId, List<SyncColumn> colunas) {
        this.nomeTabela = nomeTabela;
        this.colunaId = colunaId;
        this.colunaIdFonte = "id_" + nomeTabela + "_fonte";
        this.colunas = colunas;
    }
    
    // Getters e Setters
    public String getNomeTabela() { return nomeTabela; }
    public void setNomeTabela(String nomeTabela) { this.nomeTabela = nomeTabela; }
    
    public String getSchemaFonte() { return schemaFonte; }
    public void setSchemaFonte(String schemaFonte) { this.schemaFonte = schemaFonte; }
    
    public String getSchemaDestino() { return schemaDestino; }
    public void setSchemaDestino(String schemaDestino) { this.schemaDestino = schemaDestino; }
    
    public String getColunaId() { return colunaId; }
    public void setColunaId(String colunaId) { this.colunaId = colunaId; }
    
    public String getColunaIdFonte() { return colunaIdFonte; }
    public void setColunaIdFonte(String colunaIdFonte) { this.colunaIdFonte = colunaIdFonte; }
    
    public List<SyncColumn> getColunas() { return colunas; }
    public void setColunas(List<SyncColumn> colunas) { this.colunas = colunas; }
}