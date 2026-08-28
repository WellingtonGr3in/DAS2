package com.xltgui.corptech.itsm.snitch.triggers;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.xltgui.corptech.itsm.snitch.service.GenericSyncService;
import com.xltgui.corptech.itsm.snitch.service.SyncConfig;
import com.xltgui.corptech.itsm.snitch.service.SyncColumn;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Extract_chamado_status_historico {

    private final GenericSyncService syncService = new GenericSyncService();

    @FunctionName("extract_chamado_status_historico")
    public void extractChamadoStatusHistorico(
            @TimerTrigger(name = "timerInfo", schedule = "0 */30 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("🔄 Extraindo histórico de status de chamados - " + LocalDateTime.now());

        SyncConfig config = new SyncConfig(
                "chamado_status_historico",
                "id_chamado_status_historico",  // identificador na fonte
                Arrays.asList(
                        // ⚠️ NÃO inclua id_chamado_status_historico! É IDENTITY no destino
                        new SyncColumn("id_chamado", "id_chamado", Long.class),
                        new SyncColumn("ds_status_chamado", "ds_status_chamado", String.class),
                        new SyncColumn("dt_inicio_status", "dt_inicio_status", LocalDateTime.class),
                        new SyncColumn("dt_fim_status", "dt_fim_status", LocalDateTime.class),
                        new SyncColumn("qt_tempo_status_minutos", "qt_tempo_status_minutos", Integer.class),
                        new SyncColumn("id_analista_responsavel", "id_analista_responsavel", Integer.class),
                        new SyncColumn("id_fila", "id_fila", Integer.class),
                        new SyncColumn("dt_inclusao", "dt_inclusao", LocalDateTime.class),
                        new SyncColumn("dt_atualizacao", "dt_atualizacao", LocalDateTime.class),
                        new SyncColumn("nm_sistema_origem", "nm_sistema_origem", String.class),
                        new SyncColumn("cd_registro_origem", "cd_registro_origem", String.class)
                )
        );
        config.setColunaIdFonte("id_chamado_status_historico_fonte");  // coluna que guarda o ID fonte no destino
        config.setSchemaFonte("itsm");
        config.setSchemaDestino("itsm");

        syncService.sincronizarTabela(config, context);
        
        context.getLogger().info("✅ Extração de histórico de status de chamados concluída!");
    }
}