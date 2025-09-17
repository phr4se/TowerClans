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
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuService;
import phrase.towerClans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class MenuClanGlowService implements MenuService {

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_glow");

        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title");
        Inventory menu = Bukkit.createInventory(null, size, Utils.COLORIZER.colorize(titleMenu));

        configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_glow.items");

        for(String key : configurationSection.getKeys(false)) {

            Material material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            int slot = configurationSection.getInt(key + ".slot");
            String titleItem = Utils.COLORIZER.colorize(configurationSection.getString(key + ".title"));
            List<String> lore = configurationSection.getStringList(key + ".lore").stream().map(Utils.COLORIZER::colorize).collect(Collectors.toList());

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
