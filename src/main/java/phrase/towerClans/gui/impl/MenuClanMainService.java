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
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.config.Config;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuService;
import phrase.towerClans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

class MenuClanMainService implements MenuService {

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {

        ConfigurationSection configurationSection = Config.getFile(plugin, "menus/menu-clan-main.yml").getConfigurationSection("menu_clan_main");

        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title").replace("%clan_name%", clan.getName());
        Inventory menu = Bukkit.createInventory(null, size, Utils.COLORIZER.colorize(titleMenu));

        configurationSection = Config.getFile(plugin, "menus/menu-clan-main.yml").getConfigurationSection("menu_clan_main.items");

        for(String key : configurationSection.getKeys(false)) {

            Material material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            List<Integer> slots = configurationSection.getIntegerList(key + ".slot");
            boolean hideAttributes = configurationSection.getBoolean(key + ".hide-attributes");
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

            if(configurationSection.contains(key + ".right_click_actions") && configurationSection.contains(key + ".left_click_actions")) {

                List<String> rightClickActions = configurationSection.getStringList(key + ".right_click_actions");
                List<String> leftClickActions = configurationSection.getStringList(key + ".left_click_actions");

                ItemStack item = new ItemBuilder(material)
                        .setName(titleItem)
                        .setLore(lore)
                        .setHideAttributes(hideAttributes)
                        .setPersistentDataContainer(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING, String.join("|", rightClickActions))
                        .setPersistentDataContainer(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING, String.join("|", leftClickActions))
                        .build();

                slots.forEach(slot -> menu.setItem(slot, item));
                continue;

            }

            if(configurationSection.contains(key + ".right_click_actions")) {

                List<String> rightClickActions = configurationSection.getStringList(key + ".right_click_actions");

                ItemStack item = new ItemBuilder(material)
                        .setName(titleItem)
                        .setLore(lore)
                        .setHideAttributes(hideAttributes)
                        .setPersistentDataContainer(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING, String.join("|", rightClickActions))
                        .build();

                slots.forEach(slot -> menu.setItem(slot, item));
                continue;
            }

            if(configurationSection.contains(key + ".left_click_actions")) {

                List<String> leftClickActions = configurationSection.getStringList(key + ".left_click_actions");

                ItemStack item = new ItemBuilder(material)
                        .setName(titleItem)
                        .setLore(lore)
                        .setHideAttributes(hideAttributes)
                        .setPersistentDataContainer(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING, String.join("|", leftClickActions))
                        .build();

                slots.forEach(slot -> menu.setItem(slot, item));
                continue;

            }

            ItemStack item = new ItemBuilder(material)
                    .setName(titleItem)
                    .setHideAttributes(hideAttributes)
                    .setLore(lore)
                    .build();

            slots.forEach(slot -> menu.setItem(slot, item));

        }

        return menu;

    }

    @Override
    public boolean menuPages() {
        return false;
    }
}
