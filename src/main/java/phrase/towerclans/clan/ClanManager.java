package phrase.towerclans.clan;

import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionManager;

import java.util.Map;
import java.util.Set;

public interface ClanManager<C extends AbstractClan> {
    void addClan(String name, C clan);
    void removeClan(String name);
    C getClan(String name);
    LevelManager getLevelManager();
    PermissionManager getPermissionManager();
    Map<String, C> getClans();
    C[] values();
    boolean existsClan(String name);
    Set<String> keySet();
    Set<Map.Entry<String, ClanImpl>>  entrySet();
}
