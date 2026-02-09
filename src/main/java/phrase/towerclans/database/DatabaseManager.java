package phrase.towerclans.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import phrase.towerclans.TowerClans;
import phrase.towerclans.database.impl.SQLite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private HikariDataSource hikariDataSource;
    private Database database;

    public DatabaseManager(DatabaseType databaseType, TowerClans plugin) {
        switch (databaseType) {
            case SQLITE -> {
                HikariConfig hikariConfig = new HikariConfig();
                File dbFile = new File(plugin.getDataFolder(), "database.db");
                if (!dbFile.exists()) {
                    try {
                        dbFile.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                hikariConfig.setDriverClassName("org.sqlite.JDBC");
                String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
                hikariConfig.setJdbcUrl(jdbcUrl);
                hikariDataSource = new HikariDataSource(hikariConfig);
                this.database = new SQLite(this, plugin);
                database.initTable();
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

    public Database getDatabase() {
        return database;
    }
}
