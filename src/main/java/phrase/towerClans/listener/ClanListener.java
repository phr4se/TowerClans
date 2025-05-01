package phrase.towerClans.listener;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attributes.clan.Storage;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.events.*;
import phrase.towerClans.utils.ChatUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClanListener implements Listener {
    
    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanListener(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @EventHandler
    public void onOpenMenuClanMain(OpenMenuClanMain event) {
        ItemStack item = event.getCurrentItem();
        if(item == null) {
            event.setCancelled(true);
            return;
        }

        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if(item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING) != null) {
            String action = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING);
            ClanImpl.MenuType menu = ClanImpl.MenuType.valueOf(action);
            event.setCancelled(true);
            clan.showMenu(modifiedPlayer, menu.getId());
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClanMenuMembers(OpenMenuClanMembers event) {
        ItemStack item = event.getCurrentItem();
        if(item == null) {
            event.setCancelled(true);
            return;
        }

        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if((item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING)) != null) {
            String action = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING);

            ClanImpl.MenuType menu = ClanImpl.MenuType.valueOf(action);
            event.setCancelled(true);
            clan.showMenu(modifiedPlayer, menu.getId());
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClanMenuLevel(OpenMenuClanLevel event) {
        ItemStack item = event.getCurrentItem();
        if(event.getCurrentItem() == null) {
            event.setCancelled(true);
            return;
        }

        ModifiedPlayer modifiedPlayer = event.getModifiedPlayer();
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if((item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING)) != null) {
            String action = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING);

            ClanImpl.MenuType menu = ClanImpl.MenuType.valueOf(action);
            event.setCancelled(true);
            clan.showMenu(modifiedPlayer, menu.getId());
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLevelUp(LevelUpEvent event) {
        
        new BukkitRunnable() {

            private final ClanImpl clan = (ClanImpl) event.getClan();
            
            @Override
            public void run() {
                
                new BukkitRunnable() {
                    
                    private final ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message");
                    @Override
                    public void run() {
                        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
                            String string = configurationSection.getString("notification_of_a_level_increase");
                            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
                        }
                        cancel();
                    }
                }.runTask(plugin);

                int nextLevel = clan.getLevel() + 1;
                
                clan.setLevel(nextLevel);
                
            }
        }.runTaskAsynchronously(plugin);
        
    }

    @EventHandler
    public void onOpenStorage(OpenStorageEvent event) {
        ClanImpl clan = (ClanImpl) event.getClan();
        Player player = event.getPlayer();
        Set<UUID> copyPlayers = new HashSet<>(clan.getStorage().getPlayers());
        copyPlayers.forEach(playerUUID -> {
            if(!playerUUID.equals(player.getUniqueId())) {
                clan.getStorage().getIsUpdatedInventory().add(playerUUID);
                Bukkit.getPlayer(playerUUID).openInventory(event.getStorage());
            }
        });
    }

    @EventHandler
    public void onCloseStorage(CloseStorageEvent event) {
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
