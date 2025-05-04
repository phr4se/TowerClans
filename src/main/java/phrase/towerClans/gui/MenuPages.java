package phrase.towerClans.gui;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.util.colorizer.ColorizerProvider;

import java.util.List;

public class MenuPages {

    private static int itemPerPage;
    private static ItemStack previous;
    private static ItemStack next;
    private final static ColorizerProvider colorizerProvider;

    static {
        colorizerProvider = Plugin.getColorizerProvider();
    }

    private final List<ItemStack> contents;
    private int currentPage;
    private final Inventory menu;

    public MenuPages(int currentPage, List<ItemStack> contents, Inventory menu) {
        this.currentPage = currentPage;
        this.contents = contents;
        this.menu = menu;
    }

    public Inventory get(int page) {

        for(int i = 0; i <= menu.getSize() - 1; i++) {
            if(menu.getItem(i) == null) continue;
            if(menu.getItem(i).getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("player"), PersistentDataType.STRING) == null) continue;
            menu.setItem(i, null);
        }

        int start = itemPerPage * page;
        int end = Math.min(start + itemPerPage, contents.size());

        for(int i = start; i < end; i++) {
            menu.setItem(i - start, contents.get(i));
        }

        currentPage = page;

        if(hasNextPage() || hasPreviousPage()) createButtonNavigation(menu);

        return menu;
    }

    private void createButtonNavigation(Inventory menu) {
        menu.setItem(menu.getSize() - 6, previous);
        menu.setItem(menu.getSize() - 4, next);
    }

    public boolean hasNextPage() {
        return (currentPage + 1) * itemPerPage < contents.size();
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public static void intialize(Plugin plugin) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu");
        itemPerPage = configurationSection.getInt("item_per_page");
        configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.back");
        Material materialBack = Material.matchMaterial(configurationSection.getString("material"));
        String titleBack = configurationSection.getString("title");
        previous = new ItemBuilder(materialBack)
                .setName(colorizerProvider.colorize(titleBack))
                .setPersistentDataContainer(NamespacedKey.fromString("action"), PersistentDataType.STRING, "MENU_CLAN_PREVIOUS")
                .build();
        configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.forward");
        Material materialForward = Material.matchMaterial(configurationSection.getString("material"));
        String titleForward = configurationSection.getString("title");
        next = new ItemBuilder(materialForward)
                .setName(colorizerProvider.colorize(titleForward))
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
