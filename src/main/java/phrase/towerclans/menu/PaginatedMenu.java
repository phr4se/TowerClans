package phrase.towerclans.menu;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerclans.TowerClans;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaginatedMenu {
    public static final Map<UUID, PaginatedMenu> PLAYERS = new HashMap<>();
    private static int itemPerPage;
    private static ItemStack prev;
    private static int slotPrev;
    private static ItemStack next;
    private static int slotNext;
    private final List<ItemStack> contents;
    private int currentPage;
    private final Inventory menu;
    private final int offsetRelativeZero;

    public PaginatedMenu(int currentPage, List<ItemStack> contents, Inventory menu, int offsetRelativeZero) {
        this.currentPage = currentPage;
        this.contents = contents;
        this.menu = menu;
        this.offsetRelativeZero = offsetRelativeZero;
    }

    public static PaginatedMenu register(UUID player, PaginatedMenu menuPages) {
        PLAYERS.put(player, menuPages);
        return PLAYERS.get(player);
    }

    public static void unRegister(UUID player) {
        PLAYERS.remove(player);
    }

    public static boolean isRegistered(UUID player) {
        return PLAYERS.containsKey(player);
    }

    public static PaginatedMenu getPaginatedMenu(UUID player) {
        return PLAYERS.get(player);
    }

    public Inventory getPage(int page) {
        for (int i = 0; i <= menu.getSize() - 1; i++) {
            ItemStack itemStack = menu.getItem(i);
            if (itemStack == null || !itemStack.getItemMeta().getPersistentDataContainer().getKeys().isEmpty())
                continue;
            menu.setItem(i, null);
        }
        int start = itemPerPage * page;
        int end = Math.min(start + itemPerPage, contents.size());
        for (int i = start + offsetRelativeZero; i < end; i++) menu.setItem(i - start, contents.get(i));
        currentPage = page;
        if (hasNextPage()) menu.setItem(slotNext, next);
        if (hasPreviousPage()) menu.setItem(slotPrev, prev);
        return menu;
    }

    public boolean hasNextPage() {
        return (currentPage + 1) * itemPerPage < contents.size();
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public static void initialize(TowerClans plugin) {
        final ConfigurationSection configurationSection = Config.getFile("menu-pages.yml").getConfigurationSection("menu");
        itemPerPage = configurationSection.getInt("item-per-page");
        final ConfigurationSection configurationSectionPrevious = configurationSection.getConfigurationSection("previous");
        final Material materialPrev = Material.matchMaterial(configurationSectionPrevious.getString("material"));
        slotPrev = configurationSection.getInt("slot");
        final String titlePrev = Utils.colorizer.colorize(configurationSectionPrevious.getString("title"));
        prev = new ItemBuilder(materialPrev)
                .setName(titlePrev)
                .setPersistentDataContainer(NamespacedKey.fromString("left-click-actions"), PersistentDataType.STRING, String.join("|", List.of("MENU_CLAN_PREVIOUS")))
                .setPersistentDataContainer(NamespacedKey.fromString("right-click-actions"), PersistentDataType.STRING, String.join("|", List.of("MENU_CLAN_PREVIOUS")))
                .build();
        final ConfigurationSection configurationSectionNext = configurationSection.getConfigurationSection("next");
        final Material materialNext = Material.matchMaterial(configurationSectionNext.getString("material"));
        slotNext = configurationSectionNext.getInt("slot");
        final String titleNext = Utils.colorizer.colorize(configurationSectionNext.getString("title"));
        next = new ItemBuilder(materialNext)
                .setName(titleNext)
                .setPersistentDataContainer(NamespacedKey.fromString("left-click-actions"), PersistentDataType.STRING, String.join("|", List.of("MENU_CLAN_NEXT")))
                .setPersistentDataContainer(NamespacedKey.fromString("right-click-actions"), PersistentDataType.STRING, String.join("|", List.of("MENU_CLAN_NEXT")))
                .build();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
