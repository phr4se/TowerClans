package phrase.towerClans.gui.impl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuService;

class MenuClanStorageService implements MenuService {

    @Override
    public Inventory create(ClanImpl clan, Plugin plugin) {

        Inventory menu = clan.getStorage().getInventory();
        int availableSlots = clan.getAvailableSlots() - 1;
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_storage.item");
        String materialName = configurationSection.getString("material");
        Material material = Material.matchMaterial(materialName);
        String title = configurationSection.getString("title");
        ItemStack item = new ItemBuilder(material)
                .setName(colorizerProvider.colorize(title))
                .setPersistentDataContainer(NamespacedKey.fromString("no_available"), PersistentDataType.STRING, "no_available")
                .build();

        for(int i = 0; i <= menu.getSize() - 1; i++) {
            if(i >= 0 && i <= availableSlots) continue;
            menu.setItem(i, item);
        }

        return menu;
    }
}
