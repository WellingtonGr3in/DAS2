package com.xltgui.corptech.itsm.snitch.service;

import com.microsoft.azure.functions.ExecutionContext;
import com.xltgui.corptech.itsm.snitch.config.DatabaseConfigExternal;
import com.xltgui.corptech.itsm.snitch.config.DatabaseConfigPrimary;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericSyncService {
    
    private final JdbcTemplate jdbcExterno;
    private final JdbcTemplate jdbcPersonal;
    
    public GenericSyncService() {
        this.jdbcExterno = DatabaseConfigExternal.getJdbcTemplate();
        this.jdbcPersonal = DatabaseConfigPrimary.getJdbcTemplate();
    }
    
    public void sincronizarTabela(SyncConfig config, ExecutionContext context) {
        context.getLogger().info("🔄 Sincronizando: " + config.getNomeTabela());
        
        try {
            // 1. Buscar dados da fonte
            String selectSql = String.format("SELECT * FROM %s.%s", config.getSchemaFonte(), config.getNomeTabela());
            List<Map<String, Object>> dadosFonte = jdbcExterno.queryForList(selectSql);
            context.getLogger().info("📊 Registros na fonte: " + dadosFonte.size());
            
            if (dadosFonte.isEmpty()) {
                return;
            }
            
            // 2. Processar registros (não criar tabela automaticamente, ela já existe)
            int inseridos = 0, atualizados = 0, erros = 0;
            
            for (Map<String, Object> registro : dadosFonte) {
                try {
                    if (upsertRegistro(config, registro, context)) {
                        inseridos++;
                    } else {
                        atualizados++;
                    }
                } catch (Exception e) {
                    erros++;
                    context.getLogger().warning("❌ Erro: " + e.getMessage());
                }
            }
            
            context.getLogger().info("✅ Sincronização concluída - " + config.getNomeTabela());
            context.getLogger().info("   📥 Inseridos: " + inseridos);
            context.getLogger().info("   🔄 Atualizados: " + atualizados);
            context.getLogger().info("   ❌ Erros: " + erros);
            
        } catch (Exception e) {
            context.getLogger().severe("❌ Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean upsertRegistro(SyncConfig config, Map<String, Object> registro, ExecutionContext context) {
        // Pega o valor da coluna identificadora (ex: cd_fila, cd_analista)
        String idFonte = getStringValue(registro, config.getColunaId());
        
        if (idFonte == null) {
            context.getLogger().warning("⚠️ Registro sem identificador");
            return false;
        }
        
        // Verificar se já existe
        String checkSql = String.format("SELECT COUNT(*) FROM %s.%s WHERE %s = ?", 
            config.getSchemaDestino(), config.getNomeTabela(), config.getColunaIdFonte());
        
        Integer existe = jdbcPersonal.queryForObject(checkSql, Integer.class, idFonte);
        
        if (existe == null || existe == 0) {
            // INSERT - NÃO incluir a coluna identificadora na lista de colunas normais
            StringBuilder colunas = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();
            List<Object> insertParams = new ArrayList<>();
            
            // Adiciona a coluna identificadora primeiro
            colunas.append(config.getColunaIdFonte());
            placeholders.append("?");
            insertParams.add(idFonte);
            
            // Adiciona as demais colunas (excluindo a identificadora)
            for (SyncColumn coluna : config.getColunas()) {
                // Pula a coluna que é igual ao identificador
                if (coluna.getNome().equals(config.getColunaId())) {
                    continue;
                }
                colunas.append(", ").append(coluna.getNome());
                placeholders.append(", ?");
                Object valor = registro.get(coluna.getNomeFonte());
                insertParams.add(converterValor(valor, coluna.getTipoJava()));
            }
            
            // Adiciona data_sincronizacao
            colunas.append(", data_sincronizacao");
            placeholders.append(", ?");
            insertParams.add(LocalDateTime.now());
            
            String insertSql = String.format("INSERT INTO %s.%s (%s) VALUES (%s)", 
                config.getSchemaDestino(), config.getNomeTabela(), colunas, placeholders);
            
            jdbcPersonal.update(insertSql, insertParams.toArray());
            return true;
        } else {
            // UPDATE
            StringBuilder sets = new StringBuilder();
            List<Object> updateParams = new ArrayList<>();
            
            for (SyncColumn coluna : config.getColunas()) {
                // Pula a coluna identificadora (não deve ser atualizada)
                if (coluna.getNome().equals(config.getColunaId())) {
                    continue;
                }
                if (sets.length() > 0) sets.append(", ");
                sets.append(coluna.getNome()).append(" = ?");
                Object valor = registro.get(coluna.getNomeFonte());
                updateParams.add(converterValor(valor, coluna.getTipoJava()));
            }
            
            sets.append(", data_sincronizacao = ?");
            updateParams.add(LocalDateTime.now());
            updateParams.add(idFonte);  // WHERE clause
            
            String updateSql = String.format("UPDATE %s.%s SET %s WHERE %s = ?", 
                config.getSchemaDestino(), config.getNomeTabela(), sets, config.getColunaIdFonte());
            
            jdbcPersonal.update(updateSql, updateParams.toArray());
            return false;
        }
    }
    
    private String getStringValue(Map<String, Object> registro, String chave) {
        Object valor = registro.get(chave);
        return valor != null ? valor.toString() : null;
    }
    
    private Object converterValor(Object valor, Class<?> tipo) {
        if (valor == null) return null;
        
        if (tipo == String.class) {
            return valor.toString();
        }
        if (tipo == Integer.class) {
            if (valor instanceof Number) return ((Number) valor).intValue();
            try { return Integer.parseInt(valor.toString()); } catch (Exception e) { return null; }
        }
        if (tipo == Long.class) {
            if (valor instanceof Number) return ((Number) valor).longValue();
            try { return Long.parseLong(valor.toString()); } catch (Exception e) { return null; }
        }
        if (tipo == Boolean.class) {
            if (valor instanceof Boolean) return valor;
            if (valor instanceof Number) return ((Number) valor).intValue() == 1;
            return "true".equalsIgnoreCase(valor.toString()) || "1".equals(valor.toString());
        }
        if (tipo == LocalDateTime.class) {
            if (valor instanceof java.sql.Timestamp) return ((java.sql.Timestamp) valor).toLocalDateTime();
            if (valor instanceof LocalDateTime) return valor;
            return null;
        }
        return valor.toString();
    }
}