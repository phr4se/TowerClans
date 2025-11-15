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
import phrase.towerClans.config.Config;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuService;
import phrase.towerClans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

class MenuClanLevelService implements MenuService {

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {

        ConfigurationSection configurationSection = Config.getFile(plugin, "menus/menu-clan-level.yml").getConfigurationSection("menu_clan_level");
        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title");

        Inventory menu = Bukkit.createInventory(null, size, Utils.COLORIZER.colorize(titleMenu));

        configurationSection = Config.getFile(plugin, "menus/menu-clan-level.yml").getConfigurationSection("menu_clan_level.items");

        for(String key : configurationSection.getKeys(false)) {

            Material material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            List<Integer> slots = configurationSection.getIntegerList(key + ".slot");
            boolean hideAttributes = configurationSection.getBoolean(key + ".hide-attributes");
            String titleItem = Utils.COLORIZER.colorize(configurationSection.getString(key + ".title"));
            List<String> lore = configurationSection.getStringList(key + ".lore").stream().map(Utils.COLORIZER::colorize).toList();

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

        configurationSection = Config.getFile(plugin, "menus/menu-clan-level.yml").getConfigurationSection("menu_clan_level");

        int startSlot = configurationSection.getInt("slot");
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
                        .setHideAttributes(true)
                        .build();

                while(menu.getItem(startSlot) != null && menu.getItem(startSlot).getType() != Material.AIR) startSlot++;
                menu.setItem(startSlot, item);
                startSlot++;
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
                    .setHideAttributes(true)
                    .build();

            while(menu.getItem(startSlot) != null && menu.getItem(startSlot).getType() != Material.AIR) startSlot++;
            menu.setItem(startSlot, item);
            startSlot++;

        }

        return menu;
    }

    @Override
    public boolean menuPages() {
        return false;
    }
}
