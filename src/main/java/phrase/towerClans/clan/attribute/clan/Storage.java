package phrase.towerClans.clan.attribute.clan;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.config.Config;

import java.util.*;

public class Storage {

    private final Inventory inventory;
    private final Set<UUID> players;
    private final Set<UUID> isUpdatedInventory;
    private static int size;
    private static String title;
    private static List<Integer> safeSlots;

    public Storage() {
        players = new HashSet<>();
        isUpdatedInventory = new HashSet<>();
        inventory = Bukkit.createInventory(null, size, title);
    }

    public static void initialize(Plugin plugin) {
        ConfigurationSection configurationSection = Config.getFile(plugin, "menus/menu-clan-storage.yml").getConfigurationSection("menu_clan_storage");
        size = configurationSection.getInt("size");
        title = configurationSection.getString("title");
        safeSlots = configurationSection.getIntegerList("safe_slots");
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static boolean isSafeSlots(int slot) {
        return safeSlots.contains(slot);
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public Set<UUID> getIsUpdatedInventory() {
        return isUpdatedInventory;
    }


}
