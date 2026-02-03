package phrase.towerclans.clan;

import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.permission.PermissionManager;

import java.util.Map;

public abstract class ClanManager<C extends AbstractClan> {
    public abstract void addClan(String name, C clan);
    public abstract void removeClan(String name);
    public abstract LevelManager getLevelManager();
    public abstract PermissionManager getPermissionManager();
    public abstract Map<String, C> getClans();
}
