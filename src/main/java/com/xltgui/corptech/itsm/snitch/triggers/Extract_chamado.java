package com.xltgui.corptech.itsm.snitch.triggers;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.xltgui.corptech.itsm.snitch.service.GenericSyncService;
import com.xltgui.corptech.itsm.snitch.service.SyncConfig;
import com.xltgui.corptech.itsm.snitch.service.SyncColumn;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Extract_chamado {

    private final GenericSyncService syncService = new GenericSyncService();

    @FunctionName("extract_chamado")
    public void extractChamado(
            @TimerTrigger(name = "timerInfo", schedule = "0 */30 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("🔄 Extraindo chamados - " + LocalDateTime.now());

        SyncConfig config = new SyncConfig(
                "chamado",
                "nr_chamado",
                Arrays.asList(
                        new SyncColumn("nr_chamado", "nr_chamado", String.class),
                        new SyncColumn("ds_tipo_chamado", "ds_tipo_chamado", String.class),
                        new SyncColumn("ds_status_chamado", "ds_status_chamado", String.class),
                        new SyncColumn("ds_prioridade", "ds_prioridade", String.class),
                        new SyncColumn("dt_criacao", "dt_criacao", LocalDateTime.class),
                        new SyncColumn("dt_resolucao", "dt_resolucao", LocalDateTime.class),
                        new SyncColumn("dt_ultima_atualizacao", "dt_ultima_atualizacao", LocalDateTime.class),
                        new SyncColumn("id_analista_atual", "id_analista_atual", Integer.class),
                        new SyncColumn("id_reporter", "id_reporter", Integer.class),
                        new SyncColumn("id_categoria", "id_categoria", Integer.class),
                        new SyncColumn("id_cliente_organizacao", "id_cliente_organizacao", Integer.class),
                        new SyncColumn("id_fila_atual", "id_fila_atual", Integer.class),
                        new SyncColumn("ds_titulo", "ds_titulo", String.class),
                        new SyncColumn("ds_descricao", "ds_descricao", String.class),
                        new SyncColumn("dt_inclusao", "dt_inclusao", LocalDateTime.class),
                        new SyncColumn("dt_atualizacao", "dt_atualizacao", LocalDateTime.class),
                        new SyncColumn("nm_sistema_origem", "nm_sistema_origem", String.class),
                        new SyncColumn("cd_registro_origem", "cd_registro_origem", String.class)
                )
        );
        config.setColunaIdFonte("nr_chamado");
        config.setSchemaFonte("itsm");
        config.setSchemaDestino("itsm");

        syncService.sincronizarTabela(config, context);
        
        context.getLogger().info("✅ Extração de chamados concluída!");
    }
}