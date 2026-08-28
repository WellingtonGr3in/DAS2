package com.xltgui.corptech.itsm.snitch.triggers;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.xltgui.corptech.itsm.snitch.service.GenericSyncService;
import com.xltgui.corptech.itsm.snitch.service.SyncConfig;
import com.xltgui.corptech.itsm.snitch.service.SyncColumn;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Extract_analista {

    private final GenericSyncService syncService = new GenericSyncService();

    @FunctionName("extract_analista")
    public void extractAnalista(
            @TimerTrigger(name = "timerInfo", schedule = "0 */30 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("🔄 Extraindo analistas - " + LocalDateTime.now());

        SyncConfig config = new SyncConfig(
                "analista",                    // nome da tabela
                "cd_analista",                 // coluna identificadora (chave para upsert)
                Arrays.asList(
                        new SyncColumn("nm_analista", "nm_analista", String.class),
                        new SyncColumn("ds_email", "ds_email", String.class),
                        new SyncColumn("ds_nivel", "ds_nivel", String.class),
                        new SyncColumn("id_fila_atual", "id_fila_atual", Integer.class),
                        new SyncColumn("fl_ativo", "fl_ativo", Boolean.class),
                        new SyncColumn("dt_inclusao", "dt_inclusao", LocalDateTime.class),
                        new SyncColumn("dt_atualizacao", "dt_atualizacao", LocalDateTime.class),
                        new SyncColumn("nm_sistema_origem", "nm_sistema_origem", String.class),
                        new SyncColumn("cd_registro_origem", "cd_registro_origem", String.class)
                )
        );
        config.setColunaIdFonte("cd_analista");
        config.setSchemaFonte("itsm");
        config.setSchemaDestino("itsm");

        syncService.sincronizarTabela(config, context);
        
        context.getLogger().info("✅ Extração de analistas concluída!");
    }
}