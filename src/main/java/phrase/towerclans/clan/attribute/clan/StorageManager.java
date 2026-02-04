package phrase.towerclans.clan.attribute.clan;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import phrase.towerclans.config.Config;
import phrase.towerclans.gui.impl.MenuClanStorageService;
import phrase.towerclans.util.Utils;

import java.util.*;

public class StorageManager {
    private static int size;
    private static String title;
    private static List<Integer> safeSlots;
    private final Inventory inventory;
    private final Set<UUID> players;
    private final Set<UUID> isUpdatedInventory;

    public StorageManager() {
        players = new HashSet<>();
        isUpdatedInventory = new HashSet<>();
        inventory = Bukkit.createInventory(new MenuClanStorageService(), size, title);
    }

    public static void initialize() {
        final ConfigurationSection configurationSection = Config.getFile("menus/menu-clan-storage.yml").getConfigurationSection("menu_clan_storage");
        size = configurationSection.getInt("size");
        title = Utils.COLORIZER.colorize(configurationSection.getString("title"));
        safeSlots = configurationSection.getIntegerList("safe_slots");
    }

    public static boolean isSafeSlots(int slot) {
        return safeSlots.contains(slot);
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
