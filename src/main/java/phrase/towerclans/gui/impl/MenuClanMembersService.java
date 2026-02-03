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
import phrase.towerclans.clan.attribute.player.StatsManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionManager;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.config.Config;
import phrase.towerclans.gui.ItemBuilder;
import phrase.towerclans.gui.MenuClanService;
import phrase.towerclans.gui.MenuPages;
import phrase.towerclans.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class MenuClanMembersService implements MenuClanService, InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    private final static Map<UUID, MenuPages> PLAYERS = new HashMap<>();

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        final ConfigurationSection configurationSection = Config.getFile("menus/menu-clan-members.yml").getConfigurationSection("menu_clan_members");
        final int size = configurationSection.getInt("size");
        final String title = configurationSection.getString("title");
        final Inventory menu = Bukkit.createInventory(this, size, Utils.COLORIZER.colorize(title));
        final ConfigurationSection configurationSectionItems = Config.getFile("menus/menu-clan-members.yml").getConfigurationSection("menu_clan_members.items");
        for (String key : configurationSectionItems.getKeys(false)) {
            final Material material = Material.matchMaterial(configurationSectionItems.getString(key + ".material"));
            final List<Integer> slots = configurationSectionItems.getIntegerList(key + ".slot");
            final boolean hideAttributes = configurationSectionItems.getBoolean(key + ".hide-attributes");
            final String name = Utils.COLORIZER.colorize(configurationSectionItems.getString(key + ".name"));
            final List<String> lore = configurationSectionItems.getStringList(key + ".lore").stream().map(Utils.COLORIZER::colorize).toList();
            final int amount = configurationSectionItems.getInt(key + ".amount");
            ItemBuilder itemBuilder = new ItemBuilder(material)
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

    public List<ItemStack> getContents(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        final List<ItemStack> players = new ArrayList<>();
        final ConfigurationSection configurationSection = Config.getFile("menus/menu-clan-members.yml").getConfigurationSection("menu_clan_members");
        final Material material = Material.matchMaterial(configurationSection.getString("material"));
        final String name = configurationSection.getString("name");
        final List<String> lore = configurationSection.getStringList("lore");
        final PermissionManager permissionManager = clan.getPermissionManager();
        if (permissionManager.getPermissionRowIndex() != -1) permissionManager.setPermissionRowIndex(lore.size());
        final List<String> permission = configurationSection.getStringList("permission");
        final int currentPermission = permissionManager.getPermissionsPlayer(modifiedPlayer).getCurrentPermission();
        final String cursor = configurationSection.getString("cursor");
        ;
        permission.set(currentPermission, permission.get(currentPermission).replace("%cursor%", cursor));
        lore.addAll(permission);
        final String isAvailable = configurationSection.getString("permission_is_available");
        final String notAvailable = configurationSection.getString("permission_not_available");
        final StatsManager statsManager = plugin.getStatsManager();
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            final ModifiedPlayer key = entry.getKey();
            final String currentName = Utils.COLORIZER.colorize(name.replace("%player_name%", (key.getPlayer() == null) ? Bukkit.getOfflinePlayer(key.getPlayerUUID()).getName() : key.getPlayer().getName()));
            final StatsManager.Stats playerStats = statsManager.getPlayers().get(key.getPlayerUUID());
            final List<String> currentLore = lore.stream().map(
                    string -> {
                        String replacedString = string
                                .replace("%player_rank%", entry.getValue())
                                .replace("%player_kills%", String.valueOf(playerStats.getKills()))
                                .replace("%player_deaths%", String.valueOf(playerStats.getDeaths()))
                                .replace("%base%", (key.hasPermission(PermissionType.BASE) ? isAvailable : notAvailable))
                                .replace("%glow%", (key.hasPermission(PermissionType.GLOW) ? isAvailable : notAvailable))
                                .replace("%invite%", (key.hasPermission(PermissionType.INVITE) ? isAvailable : notAvailable))
                                .replace("%kick%", (key.hasPermission(PermissionType.KICK) ? isAvailable : notAvailable))
                                .replace("%pvp%", (key.hasPermission(PermissionType.PVP) ? isAvailable : notAvailable))
                                .replace("%storage%", (key.hasPermission(PermissionType.STORAGE) ? isAvailable : notAvailable))
                                .replace("%withdraw%", (key.hasPermission(PermissionType.WITHDRAW) ? isAvailable : notAvailable))
                                .replace("%permission%", (key.hasPermission(PermissionType.PERMISSION) ? isAvailable : notAvailable))
                                .replace("%cursor%", "");
                        return Utils.COLORIZER.colorize(replacedString);
                    }
            ).collect(Collectors.toList());
            final ItemStack itemStack = new ItemBuilder(material)
                    .setName(currentName)
                    .setLore(currentLore)
                    .setHideAttributes(true)
                    .setPersistentDataContainer(NamespacedKey.fromString("player"), PersistentDataType.STRING, key.getPlayerUUID().toString())
                    .build();
            players.add(itemStack);
        }
        return players;
    }

    public static MenuPages register(UUID player, MenuPages menuPages) {
        PLAYERS.put(player, menuPages);
        return PLAYERS.get(player);
    }

    public static void unRegister(UUID player) {
        PLAYERS.remove(player);
    }

    public static boolean isRegistered(UUID player) {
        return PLAYERS.containsKey(player);
    }

    public static MenuPages getMenuPages(UUID player) {
        return PLAYERS.get(player);
    }

    @Override
    public boolean menuPages() {
        return true;
    }
}
