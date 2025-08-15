package phrase.towerClans.gui.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.clan.permission.Permission;
import phrase.towerClans.clan.permission.PermissionType;
import phrase.towerClans.gui.ItemBuilder;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.gui.MenuService;
import phrase.towerClans.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

class MenuClanMembersService implements MenuService {

    private final static Map<UUID, MenuPages> PLAYERS = new HashMap<>();

    @Override
    public Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members");
        int size = configurationSection.getInt("size");
        String titleMenu = configurationSection.getString("title_menu");

        Inventory menu = Bukkit.createInventory(null, size, Utils.COLORIZER.colorize(titleMenu));

        configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members.items");

        for(String key : configurationSection.getKeys(false)) {

            Material material = Material.matchMaterial(configurationSection.getString(key + ".material"));
            int slot = configurationSection.getInt(key + ".slot");
            String titleItem = Utils.COLORIZER.colorize(configurationSection.getString(key + ".title"));
            List<String> lore = configurationSection.getStringList(key + ".lore").stream().map(Utils.COLORIZER::colorize).toList();

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

    public List<ItemStack> getPlayers(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {

        List<ItemStack> players = new ArrayList<>();
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members");

        Material material;
        String titleItem;
        List<String> lore;
        List<String> permission;
        String cursor;
        String isAvailable;
        String notAvailable;

        material = Material.matchMaterial(configurationSection.getString("material"));
        titleItem = configurationSection.getString("title_item");
        lore = configurationSection.getStringList("lore");

        if(Permission.permissionRowIndex != -1) Permission.permissionRowIndex = lore.size();
        permission = configurationSection.getStringList("permission");
        int currentPermission = Permission.getPermissionsPlayer(modifiedPlayer).getCurrentPermission();
        cursor = configurationSection.getString("cursor");
        permission.set(currentPermission, permission.get(currentPermission).replace("%cursor%", cursor));
        lore.addAll(permission);

        isAvailable = configurationSection.getString("permission_is_available");
        notAvailable = configurationSection.getString("permission_not_available");

        for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            ModifiedPlayer key = entry.getKey();
            String currentTitle = Utils.COLORIZER.colorize(titleItem.replace("%player_name%", (key.getPlayer() == null) ? Bukkit.getOfflinePlayer(key.getPlayerUUID()).getName() : key.getPlayer().getName()));
            Stats playerStats = Stats.PLAYERS.get(key.getPlayerUUID());
            List<String> currentLore = lore.stream().map(
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

            ItemStack item = new ItemBuilder(material)
                    .setName(currentTitle)
                    .setLore(currentLore)
                    .setPersistentDataContainer(NamespacedKey.fromString("player"), PersistentDataType.STRING, key.getPlayerUUID().toString())
                    .build();

            players.add(item);
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


}
