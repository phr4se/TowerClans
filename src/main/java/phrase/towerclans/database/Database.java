package phrase.towerclans.database;

public interface Database {
    void initTable();
    void saveClans();
    void loadClans();
    void savePlayers();
    void loadPlayers();
    void savePermissions();
    void loadPermissions();
}
