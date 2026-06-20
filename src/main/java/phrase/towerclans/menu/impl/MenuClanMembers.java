package phrase.towerclans.menu.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.attribute.player.StatsManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.Permission;
import phrase.towerclans.clan.permission.PermissionManager;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.config.Config;
import phrase.towerclans.menu.*;
import phrase.towerclans.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class MenuClanMembers extends Menu implements Handler, Paginated {
    public MenuClanMembers(String fileName, TowerClans plugin, ModifiedPlayer modifiedPlayer) {
        super(fileName, plugin, modifiedPlayer);
        setupDefaultItems();
    }

    @Override
    public List<ItemStack> getContents(ModifiedPlayer modifiedPlayer, TowerClans plugin, Object... args) {
        final List<ItemStack> players = new ArrayList<>();
        final ConfigurationSection configurationSection = Config.getFile("menus/menu-clan-members.yml").getConfigurationSection("menu-clan-members");
        final Material material = Material.matchMaterial(configurationSection.getString("material"));
        final String name = configurationSection.getString("name");
        final List<String> lore = configurationSection.getStringList("lore");
        final ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        final PermissionManager permissionManager = clan.getPermissionManager();
        if (permissionManager.getPermissionRowIndex() != -1) permissionManager.setPermissionRowIndex(lore.size());
        final List<String> permission = configurationSection.getStringList("permission");
        final int currentPermission = permissionManager.getPermissionsPlayer(modifiedPlayer).getCurrentPermission();
        final String cursor = configurationSection.getString("cursor");
        permission.set(currentPermission, permission.get(currentPermission).replace("%cursor%", cursor));
        lore.addAll(permission);
        final String isAvailable = configurationSection.getString("permission-is-available");
        final String notAvailable = configurationSection.getString("permission-not-available");
        final StatsManager statsManager = plugin.getStatsManager();
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            final ModifiedPlayer key = entry.getKey();
            final String currentName = Utils.colorizer.colorize(name.replace("%player_name%", (key.getPlayer() == null) ? Bukkit.getOfflinePlayer(key.getPlayerUUID()).getName() : key.getPlayer().getName()));
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
                        return Utils.colorizer.colorize(replacedString);
                    }
            ).collect(Collectors.toList());
            final ItemStack itemStack = new ItemStack(material);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            if (material == Material.PLAYER_HEAD) {
                final SkullMeta skullMeta = (SkullMeta) itemMeta;
                skullMeta.setOwningPlayer((Bukkit.getPlayer(key.getPlayerUUID()) == null) ? Bukkit.getOfflinePlayer(key.getPlayerUUID()) : Bukkit.getPlayer(key.getPlayerUUID()));
                itemStack.setItemMeta(skullMeta);
            }
            itemMeta.setDisplayName(currentName);
            itemMeta.setLore(currentLore);
            itemMeta.getPersistentDataContainer().set(NamespacedKey.fromString("player"), PersistentDataType.STRING, key.getPlayerUUID().toString());
            itemStack.setItemMeta(itemMeta);
            players.add(itemStack);
        }
        return players;
    }

    @Override
    public void handleClick(ClickType clickType, Player player, PersistentDataContainer persistentDataContainer, Class<? extends Cancellable> clazz, Object object, Object... args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (persistentDataContainer.has(NamespacedKey.fromString("player"), PersistentDataType.STRING)) {
            if (!modifiedPlayer.hasPermission(PermissionType.PERMISSION)) {
                Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            final PermissionManager permissionManager = clan.getPermissionManager();
            switch (clickType) {
                case RIGHT -> {
                    List<String> lore = Config.getFile("menus/menu-clan-members.yml").getConfigurationSection("menu-clan-members").getStringList("permission");
                    Permission permission = permissionManager.getPermissionsPlayer(modifiedPlayer);
                    Matcher matcher = Utils.PATTERN.matcher(lore.get(permission.getCurrentPermission()));
                    PermissionType permissionType = null;
                    while (matcher.find()) {
                        try {
                            permissionType = PermissionType.valueOf(matcher.group(1).toUpperCase());
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    UUID target = UUID.fromString(persistentDataContainer.get(NamespacedKey.fromString("player"), PersistentDataType.STRING));
                    ModifiedPlayer targetModifiedPlayer = (Bukkit.getPlayer(target) == null) ? ModifiedPlayer.get(Bukkit.getOfflinePlayer(target)) : ModifiedPlayer.get(Bukkit.getPlayer(target));
                    Permission playerPermission = permissionManager.getPermissionsPlayer(targetModifiedPlayer);
                    if (playerPermission.getPermissionTypes().contains(permissionType))
                        playerPermission.clearPermissionPlayer(permissionType);
                    else playerPermission.setPermissionPlayer(permissionType);
                    try {
                        clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    modifiedPlayer.showMenu(MenuType.MENU_CLAN_MEMBERS);
                    plugin.getDatabase().savePlayer(targetModifiedPlayer);
                }
                case LEFT -> {
                    Permission permission = permissionManager.getPermissionsPlayer(modifiedPlayer);
                    int currentPermission = permission.getCurrentPermission();
                    if (permission.hasNextPermission()) permission.setCurrentPermission(currentPermission + 1);
                    else permission.setCurrentPermission(0);
                    try {
                        clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    modifiedPlayer.showMenu(MenuType.MENU_CLAN_MEMBERS);
                }
                default -> {
                    try {
                        clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public int offsetRelativeZero() {
        return configurationSection.getInt("offset-relative-zero");
    }
}
