package phrase.towerClans.clan;

import phrase.towerClans.clan.attributes.clan.Level;
import phrase.towerClans.clan.attributes.clan.Storage;
import phrase.towerClans.clan.entity.ModifiedPlayer;

import java.util.*;

public abstract class AbstractClan implements Clan {

    private String name;
    private Map<ModifiedPlayer, String> members;
    private int level;
    private int xp;
    private int balance;
    private boolean pvp;
    private final Storage storage;

    public AbstractClan(String name) {
        this.name = name;

        members = new HashMap<>();
        level = Level.levels.get(1).getLevel();
        xp = Level.levels.get(1).getXp();
        balance = 0;
        pvp = false;
        storage = new Storage();
    }

    public abstract void showMenu(ModifiedPlayer modifiedPlayer, int id);

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

    public Storage getStorage() {
        return storage;
    }
}
