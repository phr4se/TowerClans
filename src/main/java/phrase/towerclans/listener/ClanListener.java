package phrase.towerclans.listener;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerclans.TowerClans;
import phrase.towerclans.action.ActionExecutor;
import phrase.towerclans.action.ActionTransformer;
import phrase.towerclans.clan.attribute.clan.StorageManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.Permission;
import phrase.towerclans.clan.permission.PermissionManager;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.config.Config;
import phrase.towerclans.event.*;
import phrase.towerclans.glow.Glow;
import phrase.towerclans.gui.MenuType;
import phrase.towerclans.gui.impl.*;
import phrase.towerclans.util.Utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClanListener implements Listener {
    private final TowerClans plugin;

    public ClanListener(TowerClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClickMenuClanMain(ClickMenuClanMainEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            event.setCancelled(true);
            return;
        }
        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        Player player = modifiedPlayer.getPlayer();
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        if (event.isRightClick() && persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING)) {
            String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
            event.setCancelled(true);
            return;
        }
        if (event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
            String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
    }

    private static final Pattern PATTERN = Pattern.compile("%(.*?)%");

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClickClanMenuMembers(ClickMenuClanMembersEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            event.setCancelled(true);
            return;
        }
        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        Player player = modifiedPlayer.getPlayer();
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        if (event.isRightClick() && persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING)) {
            String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
            event.setCancelled(true);
        }
        if (event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
            String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
            event.setCancelled(true);
        }
        if (persistentDataContainer.has(NamespacedKey.fromString("player"), PersistentDataType.STRING)) {
            if (!modifiedPlayer.hasPermission(PermissionType.PERMISSION)) {
                Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                event.setCancelled(true);
                return;
            }
            if (event.isRightClick()) {
                List<String> lore = Config.getFile("menus/menu-clan-members.yml").getConfigurationSection("menu-clan-members").getStringList("permission");
                final PermissionManager permissionManager = plugin.getClanManager().getPermissionManager();
                Permission permission = permissionManager.getPermissionsPlayer(modifiedPlayer);
                Matcher matcher = PATTERN.matcher(lore.get(permission.getCurrentPermission()));
                PermissionType permissionType = null;
                while (matcher.find()) {
                    try {
                        permissionType = PermissionType.valueOf(matcher.group(1).toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                UUID target = UUID.fromString(persistentDataContainer.get(NamespacedKey.fromString("player"), PersistentDataType.STRING));
                ModifiedPlayer targetModifiedPlayer = (Bukkit.getPlayer(target) == null) ? ModifiedPlayer.get(Bukkit.getOfflinePlayer(target)) : ModifiedPlayer.get(Bukkit.getPlayer(target));
                if (permissionManager.getPermissionsPlayer(targetModifiedPlayer).getPermissionTypes().contains(permissionType))
                    permissionManager.getPermissionsPlayer(targetModifiedPlayer).clearPermissionPlayer(permissionType);
                else permissionManager.getPermissionsPlayer(targetModifiedPlayer).setPermissionPlayer(permissionType);
                event.setCancelled(true);
                modifiedPlayer.showMenu(MenuType.MENU_CLAN_MEMBERS);
            }
            if (event.isLeftClick()) {
                final PermissionManager permissionManager = plugin.getClanManager().getPermissionManager();
                Permission permission = permissionManager.getPermissionsPlayer(modifiedPlayer);
                int currentPermission = permission.getCurrentPermission();
                if (permission.hasNextPermission()) permission.setCurrentPermission(currentPermission + 1);
                else permission.setCurrentPermission(0);
                event.setCancelled(true);
                modifiedPlayer.showMenu(MenuType.MENU_CLAN_MEMBERS);
            }
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClickClanMenuLevel(ClickMenuClanLevelEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            event.setCancelled(true);
            return;
        }
        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        Player player = modifiedPlayer.getPlayer();
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        if (event.isRightClick() && persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING)) {
            String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
        }
        if (event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
            String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClickMenuClanGlow(ClickMenuClanGlowEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            event.setCancelled(true);
            return;
        }
        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        Player player = modifiedPlayer.getPlayer();
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        if (event.isRightClick() && persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);
            try {
                Glow.LeatherColor color = Glow.LeatherColor.valueOf(rightClickActions);
                if (!modifiedPlayer.hasPermission(PermissionType.GLOW)) {
                    Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                    return;
                }
                clan.setColor(color);
                for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet())
                    if (Glow.isEnableForPlayer(entry.getKey())) Glow.changeForPlayer(entry.getKey(), true);
            } catch (IllegalArgumentException e) {
                ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
            }
        }
        if (event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
            try {
                Glow.LeatherColor color = Glow.LeatherColor.valueOf(leftClickActions);
                if (!modifiedPlayer.hasPermission(PermissionType.GLOW)) {
                    Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                    return;
                }
                clan.setColor(color);
                for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet())
                    if (Glow.isEnableForPlayer(entry.getKey())) Glow.changeForPlayer(entry.getKey(), true);
            } catch (IllegalArgumentException e) {
                ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
            }
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(ClanJoinEvent event) {
        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        Glow.changeForPlayer(modifiedPlayer, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(ClanLeaveEvent event) {
        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        Glow.changeForPlayer(modifiedPlayer, false, ((ClanImpl) event.getClan()).getMembers());
        Player player = modifiedPlayer.getPlayer();
        if (player.getOpenInventory().getTopInventory() != null) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (inventory.getHolder() instanceof MenuClanMainService) {
                player.closeInventory();
                return;
            }
            if (inventory.getHolder() instanceof MenuClanMembersService) {
                player.closeInventory();
                return;
            }
            if (inventory.getHolder() instanceof MenuClanLevelService) {
                player.closeInventory();
                return;
            }
            if (inventory.getHolder() instanceof MenuClanStorageService) {
                player.closeInventory();
                return;
            }
            if (inventory.getHolder() instanceof MenuClanGlowService) {
                player.closeInventory();
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLevelUp(ClanLevelUpEvent event) {
        ClanImpl clan = (ClanImpl) event.getClan();
        String message = Config.getMessages().clanLevelUp();
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            if (entry.getKey().getPlayer() == null) continue;
            Utils.sendMessage(entry.getKey().getPlayer(), message);
        }
        int nextLevel = clan.getLevel() + 1;
        clan.setLevel(nextLevel);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClickMenuClanStorage(ClickMenuClanStorageEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = event.getPlayer();
        ClanImpl clan = (ClanImpl) event.getClan();
        int slot = event.getSlot();
        if (item != null) {
            PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
            if (persistentDataContainer.has(NamespacedKey.fromString("no_available"), PersistentDataType.STRING)) {
                event.setCancelled(true);
                return;
            }
            if (StorageManager.isSafeSlots(slot)) {
                if (event.isRightClick() && persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING)) {
                    String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);
                    ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
                    event.setCancelled(true);
                    return;
                }
                if (event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
                    String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
                    ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
                    event.setCancelled(true);
                    return;
                }
                event.getStorage().setItem(slot, null);
                event.setCancelled(true);
                return;
            }
        }
        if (StorageManager.isSafeSlots(slot)) {
            event.getStorage().setItem(slot, null);
            event.setCancelled(true);
            return;
        }
        Set<UUID> copyPlayers = new HashSet<>(clan.getStorageManager().getPlayers());
        copyPlayers.forEach(playerUUID -> {
            if (!playerUUID.equals(player.getUniqueId())) {
                clan.getStorageManager().getIsUpdatedInventory().add(playerUUID);
                Bukkit.getPlayer(playerUUID).openInventory(event.getStorage());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCloseMenuClanStorage(CloseMenuClanStorageEvent event) {
        Player player = event.getPlayer();
        ClanImpl clan = (ClanImpl) event.getClan();
        StorageManager storage = clan.getStorageManager();
        storage.getInventory().setContents((event.getStorage().getContents()));
        if (!storage.getIsUpdatedInventory().contains(player.getUniqueId())) {
            storage.getPlayers().remove(player.getUniqueId());
            storage.getIsUpdatedInventory().remove(player.getUniqueId());
            return;
        }
        storage.getIsUpdatedInventory().remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClanPvp(ClanPvpEvent event) {
        ClanImpl clan = (ClanImpl) event.getClan();
        if(clan.isPvp()) event.setCancelled(true);
    }
}
