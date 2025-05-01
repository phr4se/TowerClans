package phrase.towerClans.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attributes.clan.Level;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.attributes.player.Stats;
import phrase.towerClans.clan.attributes.clan.Storage;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.commands.impl.invite.PlayerCalls;
import phrase.towerClans.events.*;
import phrase.towerClans.utils.ChatUtil;
import phrase.towerClans.utils.colorizer.ColorizerProvider;

import java.util.*;

public class PlayerListener implements Listener {

    private final Plugin plugin;
    private final ChatUtil chatUtil;
    private final PluginManager pluginManager;
    private final static ColorizerProvider colorizerProvider;
    private final int plusXp;

    static {
        colorizerProvider = Plugin.getColorizerProvider();
    }

    public PlayerListener(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = plugin.getServer().getPluginManager();
        chatUtil = new ChatUtil(plugin);
        plusXp = plugin.getConfig().getInt("settings.xp_for_murder");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) return;

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 1, plugin), event.getInventory())) pluginManager.callEvent(new ClickMenuClanMainEvent(modifiedPlayer, event));

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 2, plugin), event.getInventory())) pluginManager.callEvent(new ClickMenuClanMembersEvent(modifiedPlayer, event));

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 3, plugin), event.getInventory())) pluginManager.callEvent(new ClickMenuClanLevelEvent(modifiedPlayer, event));

        if(ClanImpl.MenuType.identical(clan.getStorage().getInventory(), event.getInventory())) pluginManager.callEvent(new OpenStorageEvent(clan, player, event.getInventory()));

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if(clan == null) return;
        Storage storage = clan.getStorage();
        if(storage.getPlayers().contains(player.getUniqueId())) pluginManager.callEvent(new CloseStorageEvent(clan, player, event.getInventory()));
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {

        new BukkitRunnable() {

            @Override
            public void run() {

                if(!(event.getDamager() instanceof Player)) {
                    cancel();
                    return;
                }

                Player attacker = (Player) event.getDamager();

                if(!(event.getEntity() instanceof Player)) {
                    cancel();
                    return;
                }

                Player defender = (Player) event.getEntity();

                ModifiedPlayer attackerModifiedPlayer = ModifiedPlayer.get(attacker);
                ModifiedPlayer defenderModifiedPlayer = ModifiedPlayer.get(defender);

                ClanImpl attackerClan = (ClanImpl) attackerModifiedPlayer.getClan();
                ClanImpl defenderClan = (ClanImpl) defenderModifiedPlayer.getClan();
                if(attackerClan == null || defenderClan == null) {
                    cancel();
                    return;
                }

                if(!attackerClan.getName().equals(defenderClan.getName())) {
                    cancel();
                    return;
                }

                if(attackerClan.isPvp()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            event.setCancelled(true);
                            cancel();
                        }
                    }.runTask(plugin);
                }

                cancel();

            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        new BukkitRunnable() {

            @Override
            public void run() {

                Player player = event.getEntity().getKiller();
                if(player == null) {
                    cancel();
                    return;
                }

                if(event.getEntity() instanceof Player) {
                    cancel();
                    return;
                }

                ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
                if(modifiedPlayer.getClan() == null) {
                    cancel();
                    return;
                }

                ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
                clan.setXp(clan.getXp() + plusXp);

                int nextLevel = clan.getLevel() + 1;
                int xp = Level.getXpLevel(nextLevel);
                if(clan.getXp() >= xp) pluginManager.callEvent(new LevelUpEvent(clan));
            }
        }.runTaskAsynchronously(plugin);

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {

                Player player = event.getEntity().getKiller();
                Player targetPlayer = event.getEntity();

                Stats targetPlayerStats = Stats.PLAYERS.get(targetPlayer.getUniqueId());
                if(player == null) {
                    targetPlayerStats.setDeaths(targetPlayerStats.getDeaths() + 1);
                    cancel();
                    return;
                }
                Stats playerStats = Stats.PLAYERS.get(player.getUniqueId());
                targetPlayerStats.setDeaths(targetPlayerStats.getDeaths() + 1);
                playerStats.setKills(playerStats.getKills() + 1);
                cancel();

            }

        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                UUID playerUUID = player.getUniqueId();
                PlayerCalls.removeQuitPlayers(playerUUID);
                ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
                ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
                if(clan == null) return;
                Storage storage = clan.getStorage();
                if(storage.getPlayers().contains(playerUUID)) storage.getPlayers().remove(playerUUID);
                if(storage.getIsUpdatedInventory().contains(playerUUID)) storage.getIsUpdatedInventory().remove(playerUUID);
                cancel();
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {

                UUID player = event.getPlayer().getUniqueId();

                if(Stats.PLAYERS.containsKey(player)) {
                    cancel();
                    return;
                }

                Stats.PLAYERS.put(player, new Stats(0, 0));
                cancel();
            }
        }.runTaskAsynchronously(plugin);

    }

}
