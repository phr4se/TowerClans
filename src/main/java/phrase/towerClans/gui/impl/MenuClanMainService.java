package phrase.towerClans.gui.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuService;
import phrase.towerClans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

class MenuClanMainService implements MenuService {

    @Override
    public Inventory create(ClanImpl clan, Plugin plugin) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_main");

        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title").replace("%clan_name%", clan.getName());
        Inventory menu = Bukkit.createInventory(null, size, Utils.COLORIZER.colorize(titleMenu));

        configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_main.items");

        for(String key : configurationSection.getKeys(false)) {

            Material material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            int slot = configurationSection.getInt(key + ".slot");
            String titleItem = Utils.COLORIZER.colorize(configurationSection.getString(key + ".title"));
            List<String> lore = configurationSection.getStringList(key + ".lore").stream().map(
                    string -> {
                        String replacedString = string
                                .replace("%name%", clan.getName())
                                .replace("%members%", String.valueOf(clan.getMembers().size()))
                                .replace("%maximum_members%", String.valueOf(Level.getLevelMaximumMembers(clan.getLevel())))
                                .replace("%level%", String.valueOf(clan.getLevel()))
                                .replace("%xp%", String.valueOf(clan.getXp()))
                                .replace("%balance%", String.valueOf(clan.getBalance()))
                                .replace("%pvp%", (clan.isPvp()) ? "Да" : "Нет")
                                .replace("%maximum_balance%", String.valueOf(Level.getLevelMaximumBalance(clan.getLevel())))
                                .replace("%kills%", String.valueOf(Stats.getKillsMembers(clan.getMembers())))
                                .replace("%deaths%", String.valueOf(Stats.getDeathsMembers(clan.getMembers())));
                        return Utils.COLORIZER.colorize(replacedString);
                    }
            ).collect(Collectors.toList());

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
