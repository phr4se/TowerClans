package phrase.towerClans.gui.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attributes.player.Stats;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class MenuClanMembersService implements MenuService {

    @Override
    public Inventory create(ClanImpl clan, Plugin plugin) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members");
        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title_menu");

        Inventory menu = Bukkit.createInventory(null, size, colorizerProvider.colorize(titleMenu));

        Material material;
        int slot;
        String titleItem;
        List<String> lore;

        material = Material.matchMaterial(configurationSection.getString("material"));
        slot = configurationSection.getInt("slot");
        titleItem = configurationSection.getString("title_item");
        lore = configurationSection.getStringList("lore");

        for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            ModifiedPlayer modifiedPlayer = entry.getKey();
            String currentTitle = colorizerProvider.colorize(titleItem.replace("%player_name%", modifiedPlayer.getPlayer().getName()));
            Stats playerStats = Stats.PLAYERS.get(modifiedPlayer.getPlayerUUID());
            List<String> currentLore = lore.stream().map(
                    string -> {
                        String replacedString = string
                                .replace("%player_rank%", entry.getValue())
                                .replace("%player_kills%", String.valueOf(playerStats.getKills()))
                                .replace("%player_deaths%", String.valueOf(playerStats.getDeaths()));
                        return colorizerProvider.colorize(replacedString);
                    }
            ).collect(Collectors.toList());

            ItemStack item = new ItemBuilder(material)
                    .setName(currentTitle)
                    .setLore(currentLore)
                    .build();

            menu.setItem(slot, item);
            slot++;
        }

        configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members.items");

        for(String key : configurationSection.getKeys(false)) {

            material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            slot = configurationSection.getInt(key + ".slot");
            titleItem = colorizerProvider.colorize(configurationSection.getString(key + ".title"));
            lore = configurationSection.getStringList(key + ".lore").stream().map(colorizerProvider::colorize).toList();

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
}
