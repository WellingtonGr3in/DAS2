package com.xltgui.corptech.itsm.snitch.triggers;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.xltgui.corptech.itsm.snitch.service.GenericSyncService;
import com.xltgui.corptech.itsm.snitch.service.SyncConfig;
import com.xltgui.corptech.itsm.snitch.service.SyncColumn;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Extract_csat_avaliacao {

    private final GenericSyncService syncService = new GenericSyncService();

    @FunctionName("extract_csat_avaliacao")
    public void extractCsatAvaliacao(
            @TimerTrigger(name = "timerInfo", schedule = "0 */30 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("🔄 Extraindo avaliações CSAT - " + LocalDateTime.now());

        SyncConfig config = new SyncConfig(
                "csat_avaliacao",
                "id_csat_avaliacao",
                Arrays.asList(
                        new SyncColumn("id_chamado", "id_chamado", Long.class),
                        new SyncColumn("id_analista", "id_analista", Integer.class),
                        new SyncColumn("nr_score", "nr_score", Integer.class),
                        new SyncColumn("ds_comentario", "ds_comentario", String.class),
                        new SyncColumn("dt_avaliacao", "dt_avaliacao", LocalDateTime.class),
                        new SyncColumn("dt_inclusao", "dt_inclusao", LocalDateTime.class),
                        new SyncColumn("dt_atualizacao", "dt_atualizacao", LocalDateTime.class),
                        new SyncColumn("nm_sistema_origem", "nm_sistema_origem", String.class),
                        new SyncColumn("cd_registro_origem", "cd_registro_origem", String.class)
                )
        );
        config.setColunaIdFonte("id_csat_avaliacao_fonte");
        config.setSchemaFonte("itsm");
        config.setSchemaDestino("itsm");

        syncService.sincronizarTabela(config, context);
        
        context.getLogger().info("✅ Extração de avaliações CSAT concluída!");
    }
}