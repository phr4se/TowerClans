package phrase.towerclans.clan.attribute.clan;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.config.Config;
import phrase.towerclans.menu.Menu;
import phrase.towerclans.menu.MenuType;
import phrase.towerclans.menu.impl.MenuClanStorage;

import java.util.*;

public class ClanImplStorage {
    private static final MenuType TYPE = MenuType.MENU_CLAN_STORAGE;
    private static List<Integer> SAFE_SLOTS;
    private final Menu menu;
    private final Inventory inventory;
    private final Set<UUID> players;
    private final Set<UUID> isUpdatedInventory;

    public ClanImplStorage(ClanImpl clan, TowerClans plugin) {
        this.players = new HashSet<>();
        this.isUpdatedInventory = new HashSet<>();
        this.menu = new MenuClanStorage(TYPE.getFileName(), plugin, clan);
        this.inventory = menu.getInventory();
    }

    public static void initialize() {
        String fileName = TYPE.getFileName();
        final ConfigurationSection configurationSection = Config.getFile(fileName).getConfigurationSection(fileName.substring(6, fileName.length() - 4));
        SAFE_SLOTS = configurationSection.getIntegerList("safe-slots");
    }

    public static boolean isSafeSlots(int slot) {
        return SAFE_SLOTS.contains(slot);
    }

    public static int getFirstFreeSlot(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) return i;
        }
        return -1;
    }

    public Menu getMenu() {
        return menu;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public Set<UUID> getIsUpdatedInventory() {
        return isUpdatedInventory;
    }
}
