package phrase.towerclans.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.attribute.player.StatsManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.config.Config;
import phrase.towerclans.menu.impl.MenuClanStorage;
import phrase.towerclans.util.Utils;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Menu implements InventoryHolder {
    private static final Consumer<InventoryClickEvent> ON_CLICK = (event -> {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClickType clickType = event.getClick();
        ItemStack itemStack = event.getCurrentItem();
        Inventory inventory = event.getView().getTopInventory();
        InventoryHolder holder = inventory.getHolder();
        Handler handler = (holder instanceof Handler o) ? o : null;
        if (itemStack == null) {
            if (inventory.getType() != InventoryType.ANVIL) {
                if (handler == null) {
                    return;
                }
                Menu menu = (holder instanceof Menu o) ? o : null;
                if (menu != null) {
                    if (!(menu instanceof MenuClanStorage)) {
                        return;
                    }
                }
            }
        }
        int slot = event.getSlot();
        int rawSlot = event.getRawSlot();
        boolean isShiftClick = event.isShiftClick();
        PersistentDataContainer persistentDataContainer = null;
        if (itemStack != null) persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        modifiedPlayer.handleDefaultClick(clickType, player, persistentDataContainer, event.getClass(), event, handler, itemStack, slot, isShiftClick, inventory, rawSlot);
    });
    private static final Consumer<InventoryDragEvent> ON_DRAG = (event -> {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        modifiedPlayer.handleDefaultDrag(event.getRawSlots(), event.getClass(), event, (holder instanceof Handler handler) ? handler : null);
    });
    private static final Consumer<InventoryCloseEvent> ON_CLOSE = (event -> {
        Player player = (Player) event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        Inventory inventory = event.getView().getTopInventory();
        InventoryHolder holder = inventory.getHolder();
        modifiedPlayer.handleDefaultClose(modifiedPlayer, inventory.getContents(), (holder instanceof Handler handler) ? handler : null);
    });
    private static final Consumer<InventoryOpenEvent> ON_OPEN = (event -> {
        Player player = (Player) event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        modifiedPlayer.handleDefaultOpen(modifiedPlayer, (holder instanceof Handler handler) ? handler : null);
    });
    protected Inventory inventory;
    protected final String fileName;
    protected TowerClans plugin;
    protected final ConfigurationSection configurationSection;
    protected ClanImpl clan = null;
    protected LevelManager levelManager;
    private StatsManager statsManager;

    public Menu(String fileName, TowerClans plugin) {
        this.fileName = fileName;
        this.plugin = plugin;
        this.configurationSection = Config.getFile(fileName).getConfigurationSection(getPathWith());
        if (configurationSection == null) return;
        int size = -1;
        if (configurationSection.contains("size")) size = configurationSection.getInt("size");
        final String title = Utils.colorizer.colorize(configurationSection.getString("title").replace("%clan_name%", (clan == null) ? "undefined" : clan.getName()));
        final InventoryType type = InventoryType.valueOf(configurationSection.getString("type", "CHEST"));
        if (type == InventoryType.CHEST) this.inventory = Bukkit.createInventory(this, size, title);
        else this.inventory = Bukkit.createInventory(this, type, title);
    }

    public Menu(String fileName, TowerClans plugin, ClanImpl clan) {
        this(fileName, plugin);
        this.clan = clan;
        this.levelManager = clan.getLevelManager();
        this.statsManager = plugin.getStatsManager();
    }

    public Menu(String fileName, TowerClans plugin, ModifiedPlayer modifiedPlayer) {
        this(fileName, plugin);
        this.clan = (ClanImpl) modifiedPlayer.getClan();
        this.levelManager = clan.getLevelManager();
        this.statsManager = plugin.getStatsManager();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private String replacePlaceholders(String string) {
        if (clan == null) return string;
        else return string
                .replace("%name%", clan.getName())
                .replace("%members%", String.valueOf(clan.getMembers().size()))
                .replace("%maximum_members%", String.valueOf(levelManager.getLevelMaximumMembers(clan.getLevel())))
                .replace("%level%", String.valueOf(clan.getLevel()))
                .replace("%xp%", String.valueOf(clan.getXp()))
                .replace("%balance%", String.valueOf(clan.getBalance()))
                .replace("%pvp%", (clan.isPvp()) ? Config.getSettings().clanPvpEnable() : Config.getSettings().clanPvpDisable())
                .replace("%maximum_balance%", String.valueOf(levelManager.getLevelMaximumBalance(clan.getLevel())))
                .replace("%kills%", String.valueOf(statsManager.getKillsMembers(clan.getMembers())))
                .replace("%deaths%", String.valueOf(statsManager.getDeathsMembers(clan.getMembers())));
    }

    public void setupItems() {
    }

    public void setupDefaultItems() {
        final ConfigurationSection configurationSectionItems = configurationSection.getConfigurationSection("items");
        for (String key : configurationSectionItems.getKeys(false)) {
            final Material material = Material.matchMaterial(configurationSectionItems.getString(key + ".material"));
            final String name = Utils.colorizer.colorize(configurationSectionItems.getString(key + ".name"));
            final boolean hideAttributes = configurationSectionItems.getBoolean(key + ".hide-attributes");
            final List<Integer> slots = configurationSectionItems.getIntegerList(key + ".slot");
            final List<String> lore = configurationSectionItems.getStringList(key + ".lore").stream().map(string -> Utils.colorizer.colorize(replacePlaceholders(string))).collect(Collectors.toList());
            final int itemStackAmount = configurationSectionItems.getInt(key + ".amount");
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
            itemStack.setAmount(itemStackAmount);
            slots.forEach(slot -> inventory.setItem(slot, itemStack));
        }
    }

    public String getPathWith() {
        if (fileName.startsWith("menus/") && fileName.endsWith(".yml"))
            return fileName.substring(6, fileName.length() - 4);
        else return fileName;
    }

    public static void onClick(InventoryClickEvent event) {
        ON_CLICK.accept(event);
    }

    public static void onDrag(InventoryDragEvent event) {
        ON_DRAG.accept(event);
    }

    public static void onClose(InventoryCloseEvent event) {
        ON_CLOSE.accept(event);
    }

    public static void onOpen(InventoryOpenEvent event) {
        ON_OPEN.accept(event);
    }
}
