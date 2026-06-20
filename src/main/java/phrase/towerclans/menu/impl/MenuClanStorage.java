package phrase.towerclans.menu.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.attribute.clan.ClanImplStorage;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.config.Config;
import phrase.towerclans.menu.Handler;
import phrase.towerclans.menu.ItemBuilder;
import phrase.towerclans.menu.Menu;
import phrase.towerclans.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MenuClanStorage extends Menu implements Handler {
    public MenuClanStorage(String fileName, TowerClans plugin, ClanImpl clan) {
        super(fileName, plugin, clan);
        setupDefaultItems();
        setupItems();
    }

    @Override
    public void setupItems() {
        int availableSlots = clan.getLevelManager().getAvailableSlots(clan.getLevel()) - 1;
        final ConfigurationSection configurationSectionItem = configurationSection.getConfigurationSection("item");
        final ItemStack noAvailableItem = new ItemBuilder(Material.matchMaterial(configurationSectionItem.getString("material")))
                .setName(Utils.colorizer.colorize(configurationSectionItem.getString("title")))
                .setHideAttributes(true)
                .setPersistentDataContainer(NamespacedKey.fromString("no_available"), PersistentDataType.STRING, "no_available")
                .build();
        for (int i = 0; i <= availableSlots; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null) continue;
            if (itemStack.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("no_available"), PersistentDataType.STRING)) inventory.setItem(i, null);
        }
        for (int i = 0; i <= inventory.getSize() - 1; i++) {
            if (i >= 0 && i <= availableSlots || ClanImplStorage.isSafeSlots(i)) continue;
            inventory.setItem(i, noAvailableItem);
        }
    }

    @Override
    public void handleClick(ClickType clickType, Player player, PersistentDataContainer persistentDataContainer, Class<? extends Cancellable> clazz, Object object, Object... args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ItemStack item = (ItemStack) args[0];
        int slot = (Integer) args[1];
        boolean isShiftClick = (Boolean) args[2];
        int rawSlot = (Integer) args[4];
        if (!modifiedPlayer.hasPermission(PermissionType.STORAGE)) {
            try {
                clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return;
        }
        if (ClanImplStorage.isSafeSlots(slot)) {
            try {
                clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (item != null) {
            if (persistentDataContainer.has(NamespacedKey.fromString("no_available"), PersistentDataType.STRING)) {
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (ClanImplStorage.isSafeSlots(slot)) {
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        if(isShiftClick) {
            if(ClanImplStorage.isSafeSlots(ClanImplStorage.getFirstFreeSlot(inventory))) {
                if(slot != rawSlot) {
                    try {
                        clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        Set<UUID> copyPlayers = new HashSet<>(clan.getClanImplStorage().getPlayers());
        copyPlayers.forEach(playerUUID -> {
            if (!playerUUID.equals(player.getUniqueId())) {
                clan.getClanImplStorage().getIsUpdatedInventory().add(playerUUID);
                Bukkit.getPlayer(playerUUID).openInventory(this.inventory);
            }
        });
        plugin.getDatabase().saveClan(clan);
    }

    @Override
    public void handleDrag(Set<Integer> rawSlots, Class<? extends Cancellable> clazz, Object object) {
        rawSlots.forEach(slot -> {
            if(ClanImplStorage.isSafeSlots(slot)) {
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void handleClose(ModifiedPlayer modifiedPlayer, ItemStack[] contents) {
        Player player = modifiedPlayer.getPlayer();
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        ClanImplStorage storage = clan.getClanImplStorage();
        storage.getInventory().setContents(contents);
        if (!storage.getIsUpdatedInventory().contains(player.getUniqueId())) {
            storage.getPlayers().remove(player.getUniqueId());
            storage.getIsUpdatedInventory().remove(player.getUniqueId());
            return;
        }
        storage.getIsUpdatedInventory().remove(player.getUniqueId());
    }

    @Override
    public boolean allowFastMoving(ItemStack itemStack, int slot) {
        if(itemStack == null) return true;
        if(ClanImplStorage.isSafeSlots(slot)) return false;
        return itemStack.getItemMeta().getPersistentDataContainer().getKeys().isEmpty();
    }
}