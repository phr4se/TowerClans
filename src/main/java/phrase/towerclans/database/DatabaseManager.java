package phrase.towerclans.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import phrase.towerclans.TowerClans;
import phrase.towerclans.config.Config;
import phrase.towerclans.config.data.Settings;
import phrase.towerclans.database.impl.MySQL;
import phrase.towerclans.database.impl.SQLite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private HikariDataSource hikariDataSource;
    private Database database;
    private final Set<CompletableFuture<Void>> tasks = new HashSet<>();

    public DatabaseManager(DatabaseType databaseType, TowerClans plugin) {
        HikariConfig hikariConfig = new HikariConfig();
        switch (databaseType) {
            case SQLITE -> {
                File dbFile = new File(plugin.getDataFolder(), "database.db");
                if (!dbFile.exists()) {
                    try {
                        dbFile.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
                hikariConfig.setMaximumPoolSize(1);
                hikariConfig.setConnectionTimeout(5000L);
                this.hikariDataSource = new HikariDataSource(hikariConfig);
                this.database = new SQLite(plugin, this);
                this.database.createTable();
            }
            case MYSQL -> {
                Settings settings = Config.getSettings();
                hikariConfig.setJdbcUrl("jdbc:mysql://" + settings.host() + ":" + settings.port() + "/" + settings.database() + "?useSSL=" + settings.useSSL() + "&autoReconnect=true&characterEncoding=utf8&serverTimezone=UTC");
                hikariConfig.setUsername(settings.username());
                hikariConfig.setPassword(settings.password());
                hikariConfig.setMaximumPoolSize(10);
                hikariConfig.setMinimumIdle(2);
                hikariConfig.setConnectionTimeout(5000L);
                hikariConfig.setIdleTimeout(600000L);
                hikariConfig.setMaxLifetime(1800000L);
                this.hikariDataSource = new HikariDataSource(hikariConfig);
                this.database = new MySQL(plugin, this);
                this.database.createTable();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public void shutdown() throws SQLException {
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
            hikariDataSource = null;
        }
    }

    public Set<CompletableFuture<Void>> getTasks() {
        return tasks;
    }

    public Database getDatabase() {
        return database;
    }
}
