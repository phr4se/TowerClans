package phrase.towerclans.database;

import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;

public interface Database {
    void createTable();
    void saveClan(ClanImpl clan);
    void saveClans();
    void removeClan(String clan);
    void loadClans();
    void savePlayer(ModifiedPlayer modifiedPlayer);
    void savePlayers();
    void loadPlayers();
    void saveAll();
    void loadAll();
}
