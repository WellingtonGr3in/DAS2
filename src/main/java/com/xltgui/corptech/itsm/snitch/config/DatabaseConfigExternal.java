package com.xltgui.corptech.itsm.snitch.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DatabaseConfigExternal {
    
    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    
    private DatabaseConfigExternal() {
        // Construtor privado para evitar instanciação
    }
    
    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            try {
                String sqlServer = System.getenv("SQL_SERVER_SOURCE");
                String sqlDatabase = System.getenv("SQL_DATABASE_SOURCE");
                String sqlUser = System.getenv("SQL_USER_SOURCE");
                String sqlPass = System.getenv("SQL_PASSWORD_SOURCE");
                
                if (sqlServer == null || sqlDatabase == null || sqlUser == null || sqlPass == null) {
                    throw new IllegalStateException("Variáveis de ambiente do banco não configuradas!");
                }
                
                String jdbcUrl = String.format(
                    "jdbc:sqlserver://%s:1433;databaseName=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30",
                    sqlServer, sqlDatabase
                );
                
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(sqlUser);
                config.setPassword(sqlPass);
                config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                config.setMaximumPoolSize(5);
                config.setMinimumIdle(1);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);
                config.setPoolName("HikariPool-ITSM");
                
                dataSource = new HikariDataSource(config);
                System.out.println("✅ DataSource configurado com sucesso!");
                
            } catch (Exception e) {
                System.err.println("❌ Erro ao configurar DataSource: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return dataSource;
    }
    
    public static synchronized JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(getDataSource());
            System.out.println("✅ JdbcTemplate configurado com sucesso!");
        }
        return jdbcTemplate;
    }
    
    public static void closeDataSource() {
        if (dataSource != null) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            if (!hikariDataSource.isClosed()) {
                hikariDataSource.close();
                System.out.println("✅ DataSource fechado com sucesso!");
            }
        }
    }
}