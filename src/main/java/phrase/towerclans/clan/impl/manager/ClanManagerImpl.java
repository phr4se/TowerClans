package phrase.towerclans.clan.impl.manager;

import phrase.towerclans.Plugin;
import phrase.towerclans.clan.ClanManager;
import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClanManagerImpl extends ClanManager<ClanImpl> {
    private final Map<String, ClanImpl> clans;
    private final PermissionManager permissionManager = new PermissionManager();
    private final LevelManager levelManager;

    public ClanManagerImpl(Plugin plugin) {
        this.clans = new HashMap<>();
        this.levelManager = new LevelManager(plugin);
    }

    @Override
    public void addClan(String name, ClanImpl clan) {
        this.clans.put(name, clan);
    }

    @Override
    public void removeClan(String name) {
        this.clans.remove(name);
    }

    @Override
    public ClanImpl getClan(String name) {
        return this.clans.get(name);
    }

    @Override
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    @Override
    public LevelManager getLevelManager() {
        return levelManager;
    }

    @Override
    public Map<String, ClanImpl> getClans() {
        return Collections.unmodifiableMap(clans);
    }
}
