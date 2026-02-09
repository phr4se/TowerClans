package phrase.towerclans.gui.impl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.attribute.clan.StorageManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.config.Config;
import phrase.towerclans.gui.ItemBuilder;
import phrase.towerclans.gui.MenuClanService;
import phrase.towerclans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class MenuClanStorageService implements MenuClanService, InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, TowerClans plugin) {
        final Inventory menu = clan.getStorageManager().getInventory();
        int availableSlots = clan.getLevelManager().getAvailableSlots(clan.getLevel()) - 1;
        final ConfigurationSection configurationSection = Config.getFile("menus/menu-clan-storage.yml").getConfigurationSection("menu-clan-storage");
        final ConfigurationSection configurationSectionItem = configurationSection.getConfigurationSection("item");
        final ItemStack noAvailableItem = new ItemBuilder(Material.matchMaterial(configurationSectionItem.getString("material")))
                .setName(Utils.COLORIZER.colorize(configurationSectionItem.getString("title")))
                .setHideAttributes(true)
                .setPersistentDataContainer(NamespacedKey.fromString("no_available"), PersistentDataType.STRING, "no_available")
                .build();
        for (int i = 0; i <= availableSlots; i++) {
            if (menu.getItem(i) == null) continue;
            if (menu.getItem(i).getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("no_available"), PersistentDataType.STRING)) {
                menu.setItem(i, null);
            }
        }
        for (int i = 0; i <= menu.getSize() - 1; i++) {
            if (i >= 0 && i <= availableSlots || StorageManager.isSafeSlots(i)) continue;
            menu.setItem(i, noAvailableItem);
        }
        final ConfigurationSection configurationSectionItems = configurationSection.getConfigurationSection("items");
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
            if (configurationSectionItems.contains(key + ".right-click-actions")) {
                final List<String> rightClickActions = configurationSectionItems.getStringList(key + ".right-click-actions");
                itemBuilder.setPersistentDataContainer(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING, String.join("|", rightClickActions));
            }
            if (configurationSectionItems.contains(key + ".left-click-actions")) {
                final List<String> leftClickActions = configurationSectionItems.getStringList(key + ".left-click-actions");
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
