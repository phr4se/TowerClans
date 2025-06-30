package phrase.towerClans.gui.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.gui.MenuService;
import phrase.towerClans.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

class MenuClanMembersService implements MenuService {

    private final static Map<UUID, MenuPages> PLAYERS = new HashMap<>();

    @Override
    public Inventory create(ClanImpl clan, Plugin plugin) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members");
        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title_menu");

        Inventory menu = Bukkit.createInventory(null, size, Utils.COLORIZER.colorize(titleMenu));

        configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members.items");

        for(String key : configurationSection.getKeys(false)) {

            Material material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            int slot = configurationSection.getInt(key + ".slot");
            String titleItem = Utils.COLORIZER.colorize(configurationSection.getString(key + ".title"));
            List<String> lore = configurationSection.getStringList(key + ".lore").stream().map(Utils.COLORIZER::colorize).toList();

            if(configurationSection.contains(key + ".actions_when_clicking")) {

                String action = configurationSection.getString(key + ".actions_when_clicking");

                ItemStack item = new ItemBuilder(material)
                        .setName(titleItem)
                        .setLore(lore)
                        .setPersistentDataContainer(NamespacedKey.fromString("action"), PersistentDataType.STRING, action)
                        .build();

                menu.setItem(slot, item);
                continue;

            }

            ItemStack item = new ItemBuilder(material)
                    .setName(titleItem)
                    .setLore(lore)
                    .build();

            menu.setItem(slot, item);

        }

        return menu;

    }

    public List<ItemStack> getPlayers(ClanImpl clan, Plugin plugin) {

        List<ItemStack> players = new ArrayList<>();
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members");

        Material material;
        String titleItem;
        List<String> lore;

        material = Material.matchMaterial(configurationSection.getString("material"));
        titleItem = configurationSection.getString("title_item");
        lore = configurationSection.getStringList("lore");

        for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            ModifiedPlayer modifiedPlayer = entry.getKey();
            String currentTitle = Utils.COLORIZER.colorize(titleItem.replace("%player_name%", (modifiedPlayer.getPlayer() == null) ? Bukkit.getOfflinePlayer(modifiedPlayer.getPlayerUUID()).getName() : modifiedPlayer.getPlayer().getName()));
            Stats playerStats = Stats.PLAYERS.get(modifiedPlayer.getPlayerUUID());
            List<String> currentLore = lore.stream().map(
                    string -> {
                        String replacedString = string
                                .replace("%player_rank%", entry.getValue())
                                .replace("%player_kills%", String.valueOf(playerStats.getKills()))
                                .replace("%player_deaths%", String.valueOf(playerStats.getDeaths()));
                        return Utils.COLORIZER.colorize(replacedString);
                    }
            ).collect(Collectors.toList());

            ItemStack item = new ItemBuilder(material)
                    .setName(currentTitle)
                    .setLore(currentLore)
                    .setPersistentDataContainer(NamespacedKey.fromString("player"), PersistentDataType.STRING, "player")
                    .build();

            players.add(item);
        }

        return players;

    }

    public static MenuPages register(UUID player, MenuPages menuPages) {
        PLAYERS.put(player, menuPages);
        return PLAYERS.get(player);
    }

    public static void unRegister(UUID player) {
        PLAYERS.remove(player);
    }

    public static boolean isRegistered(UUID player) {
        return PLAYERS.containsKey(player);
    }

    public static MenuPages getMenuPages(UUID player) {
        return PLAYERS.get(player);
    }


}
