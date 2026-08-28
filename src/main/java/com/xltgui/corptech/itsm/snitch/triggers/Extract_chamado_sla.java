package com.xltgui.corptech.itsm.snitch.triggers;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.xltgui.corptech.itsm.snitch.service.GenericSyncService;
import com.xltgui.corptech.itsm.snitch.service.SyncConfig;
import com.xltgui.corptech.itsm.snitch.service.SyncColumn;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Extract_chamado_sla {

    private final GenericSyncService syncService = new GenericSyncService();

    @FunctionName("extract_chamado_sla")
    public void extractChamadoSla(
            @TimerTrigger(name = "timerInfo", schedule = "0 */30 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("🔄 Extraindo chamados SLA - " + LocalDateTime.now());

        SyncConfig config = new SyncConfig(
                "chamado_sla",
                "id_chamado_sla",  // ← usar a coluna real da fonte como identificador
                Arrays.asList(
                        // Guarda o ID da fonte em uma coluna separada
                        new SyncColumn("id_chamado", "id_chamado", Long.class),
                        new SyncColumn("id_sla", "id_sla", Integer.class),
                        new SyncColumn("fl_breach", "fl_breach", Boolean.class),
                        new SyncColumn("qt_tempo_restante_minutos", "qt_tempo_restante_minutos", Integer.class),
                        new SyncColumn("qt_tempo_decorrido_minutos", "qt_tempo_decorrido_minutos", Integer.class),
                        new SyncColumn("qt_meta_minutos", "qt_meta_minutos", Integer.class),
                        new SyncColumn("dt_referencia", "dt_referencia", LocalDateTime.class),
                        new SyncColumn("dt_inclusao", "dt_inclusao", LocalDateTime.class),
                        new SyncColumn("dt_atualizacao", "dt_atualizacao", LocalDateTime.class),
                        new SyncColumn("nm_sistema_origem", "nm_sistema_origem", String.class),
                        new SyncColumn("cd_registro_origem", "cd_registro_origem", String.class)
                )
        );
        config.setColunaIdFonte("id_chamado_sla_fonte");  // coluna que guarda o ID fonte no destino
        config.setSchemaFonte("itsm");
        config.setSchemaDestino("itsm");

        syncService.sincronizarTabela(config, context);
        
        context.getLogger().info("✅ Extração de chamados SLA concluída!");
    }
}