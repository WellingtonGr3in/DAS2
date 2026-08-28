package com.xltgui.corptech.itsm.snitch.triggers;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.xltgui.corptech.itsm.snitch.service.GenericSyncService;
import com.xltgui.corptech.itsm.snitch.service.SyncConfig;
import com.xltgui.corptech.itsm.snitch.service.SyncColumn;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Extract_categoria {

    private final GenericSyncService syncService = new GenericSyncService();

    @FunctionName("extract_categoria")
    public void extractCategoria(
            @TimerTrigger(name = "timerInfo", schedule = "0 */30 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("🔄 Extraindo categorias - " + LocalDateTime.now());

        SyncConfig config = new SyncConfig(
                "categoria",
                "cd_categoria",
                Arrays.asList(
                        new SyncColumn("cd_categoria", "cd_categoria", String.class),
                        new SyncColumn("nm_categoria", "nm_categoria", String.class),
                        new SyncColumn("ds_descricao", "ds_descricao", String.class),
                        new SyncColumn("fl_ativo", "fl_ativo", Boolean.class),
                        new SyncColumn("dt_inclusao", "dt_inclusao", LocalDateTime.class),
                        new SyncColumn("dt_atualizacao", "dt_atualizacao", LocalDateTime.class),
                        new SyncColumn("nm_sistema_origem", "nm_sistema_origem", String.class),
                        new SyncColumn("cd_registro_origem", "cd_registro_origem", String.class)
                )
        );
        config.setColunaIdFonte("cd_categoria");
        config.setSchemaFonte("itsm");
        config.setSchemaDestino("itsm");

        syncService.sincronizarTabela(config, context);
        
        context.getLogger().info("✅ Extração de categorias concluída!");
    }
}