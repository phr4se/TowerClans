package phrase.towerClans.gui.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.config.Config;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuService;
import phrase.towerClans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class MenuClanGlowService implements MenuService {

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        ConfigurationSection configurationSection = Config.getFile(plugin, "menus/menu-clan-glow.yml").getConfigurationSection("menu_clan_glow");

        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title");
        Inventory menu = Bukkit.createInventory(null, size, Utils.COLORIZER.colorize(titleMenu));

        configurationSection = Config.getFile(plugin, "menus/menu-clan-glow.yml").getConfigurationSection("menu_clan_glow.items");

        for(String key : configurationSection.getKeys(false)) {

            Material material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            List<Integer> slots = configurationSection.getIntegerList(key + ".slot");
            boolean hideAttributes = configurationSection.getBoolean(key + ".hide-attributes");
            String titleItem = Utils.COLORIZER.colorize(configurationSection.getString(key + ".title"));
            List<String> lore = configurationSection.getStringList(key + ".lore").stream().map(Utils.COLORIZER::colorize).collect(Collectors.toList());

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
                    .setLore(lore)
                    .setHideAttributes(hideAttributes)
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
