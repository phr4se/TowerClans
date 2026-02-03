package phrase.towerclans.gui.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import phrase.towerclans.Plugin;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.config.Config;
import phrase.towerclans.gui.ItemBuilder;
import phrase.towerclans.gui.MenuClanService;
import phrase.towerclans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class MenuClanGlowService implements MenuClanService, InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        final ConfigurationSection configurationSection = Config.getFile("menus/menu-clan-glow.yml").getConfigurationSection("menu_clan_glow");
        final int size = configurationSection.getInt("size");
        final String title = Utils.COLORIZER.colorize(configurationSection.getString("title"));
        final Inventory menu = Bukkit.createInventory(this, size, Utils.COLORIZER.colorize(title));
        final ConfigurationSection configurationSectionItems = Config.getFile("menus/menu-clan-glow.yml").getConfigurationSection("menu_clan_glow.items");
        for (String key : configurationSectionItems.getKeys(false)) {
            final Material material = Material.matchMaterial(configurationSectionItems.getString(key + ".material"));
            final List<Integer> slots = configurationSectionItems.getIntegerList(key + ".slot");
            final boolean hideAttributes = configurationSectionItems.getBoolean(key + ".hide-attributes");
            final String name = Utils.COLORIZER.colorize(configurationSectionItems.getString(key + ".name"));
            final List<String> lore = configurationSectionItems.getStringList(key + ".lore").stream().map(Utils.COLORIZER::colorize).collect(Collectors.toList());
            final int amount = configurationSectionItems.getInt(key + ".amount");
            final ItemBuilder itemBuilder = new ItemBuilder(material)
                    .setName(name)
                    .setLore(lore)
                    .setHideAttributes(hideAttributes);
            if (configurationSectionItems.contains(key + ".right_click_actions")) {
                final List<String> rightClickActions = configurationSectionItems.getStringList(key + ".right_click_actions");
                itemBuilder.setPersistentDataContainer(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING, String.join("|", rightClickActions));
            }
            if (configurationSectionItems.contains(key + ".left_click_actions")) {
                final List<String> leftClickActions = configurationSectionItems.getStringList(key + ".left_click_actions");
                itemBuilder.setPersistentDataContainer(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING, String.join("|", leftClickActions));
            }
            final ItemStack itemStack = itemBuilder.build();
            itemStack.setAmount(amount);
            slots.forEach(slot -> menu.setItem(slot, itemStack));
        }
        return menu;
    }

    @Override
    public boolean menuPages() {
        return false;
    }
}