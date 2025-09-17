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
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuService;
import phrase.towerClans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

class MenuClanLevelService implements MenuService {

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_level");
        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title");

        Inventory menu = Bukkit.createInventory(null, size, Utils.COLORIZER.colorize(titleMenu));
        int slot = configurationSection.getInt("slot");

        for (int level = 1; level <= Level.getCountLevels(); level++) {
            int finalLevel = level;
            if (clan.getLevel() < level) {
                Material material = Material.matchMaterial(configurationSection.getString("not_received.material"));
                String titleItem = Utils.COLORIZER.colorize(configurationSection.getString("not_received.title").replace("%level%", String.valueOf(level)));
                List<String> lore = configurationSection.getStringList("not_received.lore").stream().map(string -> {
                    String replacedString = string
                            .replace("%maximum_balance%", String.valueOf(Level.getLevelMaximumBalance(finalLevel)))
                            .replace("%maximum_members%", String.valueOf(Level.getLevelMaximumMembers(finalLevel)))
                            .replace("%available%", String.valueOf(Level.getAvailableSlots(finalLevel)));
                    return Utils.COLORIZER.colorize(replacedString);
                }).collect(Collectors.toList());

                ItemStack item = new ItemBuilder(material)
                        .setName(titleItem)
                        .setLore(lore)
                        .build();

                menu.setItem(slot, item);
                slot++;
                continue;
            }

            Material material = Material.matchMaterial(configurationSection.getString("received.material"));
            String titleItem = (Utils.COLORIZER.colorize(configurationSection.getString("received.title").replace("%level%", String.valueOf(level))));
            List<String> lore = configurationSection.getStringList("received.lore").stream().map(string -> {
                String replacedString = string
                        .replace("%maximum_balance%", String.valueOf(Level.getLevelMaximumBalance(finalLevel)))
                        .replace("%maximum_members%", String.valueOf(Level.getLevelMaximumMembers(finalLevel)))
                        .replace("%available%", String.valueOf(Level.getAvailableSlots(finalLevel)));
                return Utils.COLORIZER.colorize(replacedString);
            }).collect(Collectors.toList());

            ItemStack item = new ItemBuilder(material)
                    .setName(titleItem)
                    .setLore(lore)
                    .build();

            menu.setItem(slot, item);
            slot++;

        }

        configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_level.items");

        for(String key : configurationSection.getKeys(false)) {

            Material material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            slot = configurationSection.getInt(key + ".slot");
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

    @Override
    public boolean menuPages() {
        return false;
    }
}
