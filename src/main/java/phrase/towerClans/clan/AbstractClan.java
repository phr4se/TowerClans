package phrase.towerClans.clan;

import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.attribute.clan.Storage;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.glow.Glow;
import phrase.towerClans.gui.MenuType;

import java.util.*;

public abstract class AbstractClan implements Clan {

    private String name;
    private Map<ModifiedPlayer, String> members;
    private int level;
    private int xp;
    private final int availableSlots;
    private int balance;
    private boolean pvp;
    private final Storage storage;
    private Glow.LeatherColor color;

    public AbstractClan(String name) {
        this.name = name;

        members = new HashMap<>();
        level = 1;
        xp = Level.getXpLevel(level);
        availableSlots = Level.getAvailableSlots(level);
        balance = 0;
        pvp = false;
        storage = new Storage();
        color = Glow.LeatherColor.RED;
    }

    public abstract void showMenu(ModifiedPlayer modifiedPlayer, MenuType menuType);

    public abstract void glow(ModifiedPlayer modifiedPlayer, Plugin plugin);

    public abstract void chat(String message);

    public void setColor(Glow.LeatherColor color) {
        this.color = color;
    }

    public Glow.LeatherColor getColor() {
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

    public int getAvailableSlots() {
        return availableSlots;
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
