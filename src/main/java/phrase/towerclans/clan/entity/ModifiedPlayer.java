package phrase.towerclans.clan.entity;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerclans.TowerClans;
import phrase.towerclans.action.ActionExecutor;
import phrase.towerclans.action.ActionTransformer;
import phrase.towerclans.clan.Clan;
import phrase.towerclans.clan.ClanManager;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.menu.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ModifiedPlayer {
    public static TowerClans plugin;
    public static ClanManager<ClanImpl> clanManager;
    private final UUID playerUUID;
    private Clan clan;

    public ModifiedPlayer(UUID playerUUID, Clan clan) {
        this.playerUUID = playerUUID;
        this.clan = clan;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    private static final Map<UUID, ModifiedPlayer> CACHE;
    static {
        CACHE = new HashMap<>();
    }
    public static ModifiedPlayer get(UUID playerUUID) {
        if (CACHE.containsKey(playerUUID)) return CACHE.get(playerUUID);
        if (get(Bukkit.getOfflinePlayer(playerUUID)) == null) return get(Bukkit.getPlayer(playerUUID));
        else get(Bukkit.getOfflinePlayer(playerUUID));
        return null;
    }

    public static ModifiedPlayer get(Player player) {
        if (player == null) return null;
        UUID playerUUID = player.getUniqueId();
        if (CACHE.containsKey(playerUUID)) return CACHE.get(playerUUID);
        else {
            ModifiedPlayer modifiedPlayer = null;
            for (Map.Entry<String, ClanImpl> clan : clanManager.entrySet()) {
                for (Map.Entry<ModifiedPlayer, String> entry : clan.getValue().getMembers().entrySet()) {
                    if (!entry.getKey().getPlayerUUID().equals(playerUUID)) continue;
                    modifiedPlayer = new ModifiedPlayer(playerUUID, clan.getValue());
                }
            }
            if (modifiedPlayer == null) modifiedPlayer = new ModifiedPlayer(playerUUID, null);
            CACHE.put(playerUUID, modifiedPlayer);
            return modifiedPlayer;
        }
    }

    public static ModifiedPlayer get(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) return null;
        UUID playerUUID = offlinePlayer.getUniqueId();
        if (CACHE.containsKey(playerUUID)) return CACHE.get(playerUUID);
        else {
            ModifiedPlayer modifiedPlayer = null;
            for (Map.Entry<String, ClanImpl> clan : clanManager.entrySet()) {
                for (Map.Entry<ModifiedPlayer, String> entry : clan.getValue().getMembers().entrySet()) {
                    if (!entry.getKey().getPlayerUUID().equals(playerUUID)) continue;
                    modifiedPlayer = new ModifiedPlayer(playerUUID, clan.getValue());
                }
            }
            if (modifiedPlayer == null) modifiedPlayer = new ModifiedPlayer(playerUUID, null);
            CACHE.put(playerUUID, modifiedPlayer);
            return modifiedPlayer;
        }
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean hasPermission(PermissionType permissionType) {
        return plugin.getClanManager().getPermissionManager().getPermissionsPlayer(this).getPermissionTypes().contains(permissionType);
    }

    public void showMenu(MenuType type) {
        Menu menu = MenuFactoryImpl.getMenu(type, plugin, this);
        Player player = getPlayer();
        if (menu == null) {
            player.closeInventory();
            return;
        }
        Inventory inventory = menu.getInventory();
        if (menu instanceof Paginated paginated) {
            List<ItemStack> contents = paginated.getContents(this, plugin);
            PaginatedMenu paginatedMenu = PaginatedMenu.register(playerUUID, new PaginatedMenu(0, contents, inventory, paginated.offsetRelativeZero()));
            player.openInventory(paginatedMenu.getPage(paginatedMenu.getCurrentPage()));
        } else player.openInventory(inventory);
    }

    public void handleDefaultClick(ClickType clickType, Player player, PersistentDataContainer persistentDataContainer, Class<? extends Cancellable> clazz, Object object, Handler handler, Object... args) {
        boolean isShiftClick = (Boolean) args[2];
        if(handler != null) {
            if (isShiftClick && !handler.allowFastMoving((ItemStack) args[0], (Integer) args[1])) {
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            handler.handleClick(clickType, player, persistentDataContainer, clazz, object, args[0], args[1], isShiftClick, args[3], args[4]);
            if((ItemStack) args[0] == null) return;
        } else {
            try {
                clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        switch (clickType) {
            case RIGHT -> {
                if (!persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING)) return;
                if (persistentDataContainer.has(NamespacedKey.fromString("amount"))) {
                    try {
                        clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);
                ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            case LEFT -> {
                if (!persistentDataContainer.has(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING)) return;
                if (persistentDataContainer.has(NamespacedKey.fromString("amount"))) {
                    try {
                        clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
                ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void handleDefaultDrag(Set<Integer> rawSlots, Class<? extends Cancellable> clazz, Object object, Handler handler) {
        if(handler == null) {
            try {
                clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else handler.handleDrag(rawSlots, clazz, object);
    }

    public void handleDefaultClose(ModifiedPlayer modifiedPlayer, ItemStack[] contents, Handler handler) {
        if(handler != null) handler.handleClose(modifiedPlayer, contents);
    }

    public void handleDefaultOpen(ModifiedPlayer modifiedPlayer, Handler handler) {
        if(handler != null) handler.handleOpen(modifiedPlayer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModifiedPlayer that = (ModifiedPlayer) o;
        return Objects.equals(playerUUID, that.playerUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerUUID, clan);
    }
}
