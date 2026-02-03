package phrase.towerclans.gui;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerclans.Plugin;
import phrase.towerclans.util.Utils;

import java.util.List;

public class MenuPages {
    private static int itemPerPage;
    private static ItemStack prev;
    private static int slotPrev;
    private static ItemStack next;
    private static int slotNext;
    private final List<ItemStack> contents;
    private int currentPage;
    private final Inventory menu;

    public MenuPages(int currentPage, List<ItemStack> contents, Inventory menu) {
        this.currentPage = currentPage;
        this.contents = contents;
        this.menu = menu;
    }

    public Inventory getPage(int page) {
        for (int i = 0; i <= menu.getSize() - 1; i++) {
            if (menu.getItem(i) == null) continue;
            if (menu.getItem(i).getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("player"), PersistentDataType.STRING) == null)
                continue;
            menu.setItem(i, null);
        }
        int start = itemPerPage * page;
        int end = Math.min(start + itemPerPage, contents.size());
        for (int i = start; i < end; i++) {
            menu.setItem(i - start, contents.get(i));
        }
        currentPage = page;
        if (hasNextPage() || hasPreviousPage()) createButtonNavigation(menu);
        return menu;
    }

    private void createButtonNavigation(Inventory menu) {
        menu.setItem(slotPrev, prev);
        menu.setItem(slotNext, next);
    }

    public boolean hasNextPage() {
        return (currentPage + 1) * itemPerPage < contents.size();
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public static void initialize(Plugin plugin) {
        final ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu");
        itemPerPage = configurationSection.getInt("item_per_page");
        final ConfigurationSection configurationSectionPrevious = plugin.getConfig().getConfigurationSection("settings.menu.previous");
        final Material materialPrev = Material.matchMaterial(configurationSectionPrevious.getString("material"));
        slotPrev = configurationSection.getInt("slot");
        final String titlePrev = Utils.COLORIZER.colorize(configurationSectionPrevious.getString("title"));
        prev = new ItemBuilder(materialPrev)
                .setName(titlePrev)
                .setPersistentDataContainer(NamespacedKey.fromString("action"), PersistentDataType.STRING, "MENU_CLAN_PREVIOUS")
                .build();
        final ConfigurationSection configurationSectionNext = plugin.getConfig().getConfigurationSection("settings.menu.next");
        final Material materialNext = Material.matchMaterial(configurationSectionNext.getString("material"));
        slotNext = configurationSectionNext.getInt("slot");
        final String titleNext = Utils.COLORIZER.colorize(configurationSectionNext.getString("title"));
        next = new ItemBuilder(materialNext)
                .setName(Utils.COLORIZER.colorize(titleNext))
                .setPersistentDataContainer(NamespacedKey.fromString("action"), PersistentDataType.STRING, "MENU_CLAN_NEXT")
                .build();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
