package phrase.towerclans.clan;

import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.attribute.clan.StorageManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.permission.PermissionManager;
import phrase.towerclans.glow.Color;
import phrase.towerclans.glow.ColorManager;

import java.util.*;

public abstract class AbstractClan implements Clan {
    private String name;
    private Map<ModifiedPlayer, String> members;
    private int level;
    private int xp;
    private int balance;
    private boolean pvp;
    private final StorageManager storageManager;
    private final LevelManager levelManager;
    private Color color;
    private final PermissionManager permissionManager;

    public AbstractClan(String name, ClanManager<?> clanManager) {
        this.name = name;
        this.members = new HashMap<>();
        this.level = 1;
        this.xp = clanManager.getLevelManager().getXpLevel(level);
        this.balance = 0;
        this.pvp = false;
        this.storageManager = new StorageManager();
        this.color = ColorManager.COLOR;
        this.levelManager = clanManager.getLevelManager();
        this.permissionManager = clanManager.getPermissionManager();
    }

    public abstract void glow(ModifiedPlayer modifiedPlayer, TowerClans plugin);
    public abstract void chat(String message);

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<ModifiedPlayer, String> getMembers() {
        return members;
    }

    public void setMembers(Map<ModifiedPlayer, String> members) {
        this.members = members;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
}
