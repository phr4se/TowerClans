package phrase.towerClans.listener;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.action.ActionExecutor;
import phrase.towerClans.action.ActionTransformer;
import phrase.towerClans.clan.attribute.clan.Storage;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.clan.permission.Permission;
import phrase.towerClans.clan.permission.PermissionType;
import phrase.towerClans.config.Config;
import phrase.towerClans.event.*;
import phrase.towerClans.glow.Glow;
import phrase.towerClans.gui.MenuFactory;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.gui.MenuType;
import phrase.towerClans.gui.impl.MenuClanMembersProvider;
import phrase.towerClans.util.Utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClanListener implements Listener {

    private final Plugin plugin;

    public ClanListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
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

        if(event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
            String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

    }

    private static final Pattern PATTERN = Pattern.compile("%(.*?)%");

    @EventHandler
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

        if(event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
            String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
            event.setCancelled(true);
        }

        if(persistentDataContainer.has(NamespacedKey.fromString("player"), PersistentDataType.STRING)) {

            if(!modifiedPlayer.hasPermission(PermissionType.PERMISSION)) {
                Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                event.setCancelled(true);
                return;
            }

            if(event.isRightClick()) {

                List<String> lore = Config.getFile(plugin, "menus/menu-clan-members.yml").getConfigurationSection("menu_clan_members").getStringList("permission");

                Permission permission = Permission.getPermissionsPlayer(modifiedPlayer);

                Matcher matcher = PATTERN.matcher(lore.get(permission.getCurrentPermission()));

                PermissionType permissionType = null;
                while(matcher.find()) {
                    try {
                        permissionType = PermissionType.valueOf(matcher.group(1).toUpperCase());
                    } catch (IllegalArgumentException ignored) {}
                }

                UUID target = UUID.fromString(persistentDataContainer.get(NamespacedKey.fromString("player"), PersistentDataType.STRING));
                ModifiedPlayer targetModifiedPlayer = (Bukkit.getPlayer(target) == null) ? ModifiedPlayer.get(Bukkit.getOfflinePlayer(target)) : ModifiedPlayer.get(Bukkit.getPlayer(target));
                if (Permission.getPermissionsPlayer(targetModifiedPlayer).getPermissionTypes().contains(permissionType))
                    Permission.getPermissionsPlayer(targetModifiedPlayer).clearPermissionPlayer(permissionType);
                else Permission.getPermissionsPlayer(targetModifiedPlayer).setPermissionPlayer(permissionType);

                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, MenuType.MENU_CLAN_MEMBERS);

            }

            if(event.isLeftClick()) {

                Permission permission = Permission.getPermissionsPlayer(modifiedPlayer);
                int currentPermission = permission.getCurrentPermission();
                if(permission.hasNextPermission(plugin)) permission.setCurrentPermission(currentPermission + 1);
                else permission.setCurrentPermission(0);
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, MenuType.MENU_CLAN_MEMBERS);

            }

        }

        event.setCancelled(true);


    }


    @EventHandler
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

        if(event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
            String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
            ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
        }

        event.setCancelled(true);

    }

    @EventHandler
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

        if(event.isRightClick() && persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);

            try {
                Glow.LeatherColor color = Glow.LeatherColor.valueOf(rightClickActions);
                if(!modifiedPlayer.hasPermission(PermissionType.GLOW)) {
                    Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                    return;
                }
                clan.setColor(color);
                for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) if(Glow.isEnableForPlayer(entry.getKey())) Glow.changeForPlayer(entry.getKey(), true);
            } catch (IllegalArgumentException e) {
                ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
            }


        }

        if(event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);

            try {
                Glow.LeatherColor color = Glow.LeatherColor.valueOf(leftClickActions);
                if(!modifiedPlayer.hasPermission(PermissionType.GLOW)) {
                    Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                    return;
                }
                clan.setColor(color);
                for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) if(Glow.isEnableForPlayer(entry.getKey())) Glow.changeForPlayer(entry.getKey(), true);
            } catch (IllegalArgumentException e) {
                ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
            }


        }

        event.setCancelled(true);
    }


    @EventHandler
    public void onJoin(JoinEvent event) {

        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();

        Glow.changeForPlayer(modifiedPlayer, false);

    }

    @EventHandler
    public void onLeave(LeaveEvent event) {

        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();

        ClanImpl clan = (ClanImpl) event.getClan();

        Glow.changeForPlayer(modifiedPlayer, false, ((ClanImpl) event.getClan()).getMembers());

        Player player = modifiedPlayer.getPlayer();

        if (player.getOpenInventory().getTopInventory() != null) {

            Inventory inventory = player.getOpenInventory().getTopInventory();

            if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_MAIN).getMenu(modifiedPlayer, clan, plugin), inventory))
                player.closeInventory();

            if (((MenuClanMembersProvider) MenuFactory.getProvider(MenuType.MENU_CLAN_MEMBERS)).getMenuPages(player.getUniqueId()) != null) {
                MenuPages menuPages = ((MenuClanMembersProvider) MenuFactory.getProvider(MenuType.MENU_CLAN_MEMBERS)).getMenuPages(player.getUniqueId());
                if (identical(menuPages.getPage(menuPages.getCurrentPage()), inventory))
                    player.closeInventory();
            }

            if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_MEMBERS).getMenu(modifiedPlayer, clan, plugin), inventory))
                player.closeInventory();

            if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_LEVEL).getMenu(modifiedPlayer, clan, plugin), inventory))
                player.closeInventory();

            if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_STORAGE).getMenu(modifiedPlayer, clan, plugin), inventory))
                player.closeInventory();

            if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_GLOW).getMenu(modifiedPlayer, clan, plugin), inventory))
                player.closeInventory();

        }

    }

    private boolean identical(Inventory o1, Inventory o2) {

        ItemStack[] items1 = o1.getContents();
        ItemStack[] items2 = o2.getContents();

        if (items1.length != items2.length) return false;

        for (int i = 0; i < items1.length; i++) {
            ItemStack item1 = items1[i];
            ItemStack item2 = items2[i];

            if (item1 == null && item2 == null) continue;

            if (item1 == null || item2 == null) return false;

            if(item1.getType() != item2.getType()) return false;

            if(item1.getAmount() != item2.getAmount()) return false;

            ItemMeta itemMeta1 = item1.getItemMeta();
            ItemMeta itemMeta2 = item2.getItemMeta();

            if (itemMeta1 == null && itemMeta2 == null) continue;

            if (itemMeta1 == null || itemMeta2 == null) return false;

            if(!itemMeta1.getDisplayName().equals(itemMeta2.getDisplayName())) return false;

            if(!itemMeta1.getPersistentDataContainer().equals(itemMeta2.getPersistentDataContainer())) return false;

        }

        return true;
    }

    @EventHandler
    public void onLevelUp(LevelUpEvent event) {

        ClanImpl clan = (ClanImpl) event.getClan();

        String message = Config.getMessages().clanLevelUp();
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            if(entry.getKey().getPlayer() == null) continue;
            Utils.sendMessage(entry.getKey().getPlayer(), message);
        }

        int nextLevel = clan.getLevel() + 1;
        clan.setLevel(nextLevel);
    }

    @EventHandler
    public void onClickMenuClanStorage(ClickMenuClanStorageEvent event) {
        ItemStack item = event.getCurrentItem();

        Player player = event.getPlayer();
        ClanImpl clan = (ClanImpl) event.getClan();

        int slot = event.getSlot();

        if(item != null) {

            PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
            if (persistentDataContainer.has(NamespacedKey.fromString("no_available"), PersistentDataType.STRING)) {
                event.setCancelled(true);
                return;
            }

            if(Storage.isSafeSlots(slot)) {

                if (event.isRightClick() && persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING)) {
                    String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);
                    ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
                    event.setCancelled(true);
                    return;
                }

                if(event.isLeftClick() && persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) {
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

        if(Storage.isSafeSlots(slot)) {
            event.getStorage().setItem(slot, null);
            event.setCancelled(true);
            return;
        }

        Set<UUID> copyPlayers = new HashSet<>(clan.getStorage().getPlayers());
        copyPlayers.forEach(playerUUID -> {
            if(!playerUUID.equals(player.getUniqueId())) {
                clan.getStorage().getIsUpdatedInventory().add(playerUUID);
                Bukkit.getPlayer(playerUUID).openInventory(event.getStorage());
            }
        });

    }

    @EventHandler
    public void onCloseMenuClanStorage(CloseMenuClanStorageEvent event) {
        Player player = event.getPlayer();
        ClanImpl clan = (ClanImpl) event.getClan();
        Storage storage = clan.getStorage();
        storage.getInventory().setContents((event.getStorage().getContents()));
        if (!storage.getIsUpdatedInventory().contains(player.getUniqueId())) {
            storage.getPlayers().remove(player.getUniqueId());
            storage.getIsUpdatedInventory().remove(player.getUniqueId());
            return;
        }
        storage.getIsUpdatedInventory().remove(player.getUniqueId());
    }

}
