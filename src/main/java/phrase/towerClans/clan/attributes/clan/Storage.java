package phrase.towerClans.clan.attributes.clan;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Storage {

    private Inventory inventory;
    private final Set<UUID> players;
    private final Set<UUID> isUpdatedInventory;
    private static int size;
    private static String title;

    public Storage() {
        players = new HashSet<>();
        isUpdatedInventory = new HashSet<>();
        inventory = Bukkit.createInventory(null, size, title);
    }

    public static void intialize(Plugin plugin) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_storage");
        size = configurationSection.getInt("size");
        title = configurationSection.getString("title");
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
