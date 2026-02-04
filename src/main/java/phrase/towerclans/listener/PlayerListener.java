package phrase.towerclans.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;
import phrase.towerclans.Plugin;
import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.attribute.player.StatsManager;
import phrase.towerclans.clan.attribute.clan.StorageManager;
import phrase.towerclans.clan.event.Event;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.impl.invite.PlayerCalls;
import phrase.towerclans.config.Config;
import phrase.towerclans.event.*;
import phrase.towerclans.glow.Glow;
import phrase.towerclans.glow.GlowPacketListener;
import phrase.towerclans.gui.MenuFactory;
import phrase.towerclans.gui.MenuType;
import phrase.towerclans.gui.impl.*;
import phrase.towerclans.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {
    private final Plugin plugin;
    private final PluginManager pluginManager;

    public PlayerListener(Plugin plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) return;
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (event.getInventory().getHolder() instanceof MenuClanMainService) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanMainEvent(modifiedPlayer, event));
        }
        if (event.getInventory().getHolder() instanceof MenuClanMembersService) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanMembersEvent(modifiedPlayer, event));
            return;
        }
        if (event.getInventory().getHolder() instanceof MenuClanLevelService) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanLevelEvent(modifiedPlayer, event));
            return;
        }
        if (event.getInventory().getHolder() instanceof MenuClanStorageService) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanStorageEvent(clan, player, event.getInventory(), event));
            return;
        }
        if (event.getInventory().getHolder() instanceof MenuClanGlowService) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanGlowEvent(modifiedPlayer, event));
            return;
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan != null) {
            StorageManager storage = clan.getStorageManager();
            if (storage.getPlayers().contains(player.getUniqueId()))
                pluginManager.callEvent(new CloseMenuClanStorageEvent(clan, player, event.getInventory()));
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player defender = (Player) event.getEntity();
        if (!(event.getDamager() instanceof Player) && (!(event.getDamager() instanceof Projectile))) return;
        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                attacker = (Player) ((Projectile) event.getDamager()).getShooter();
            }
        }
        if (attacker == null) return;
        ModifiedPlayer attackerModifiedPlayer = ModifiedPlayer.get(attacker);
        ModifiedPlayer defenderModifiedPlayer = ModifiedPlayer.get(defender);
        ClanImpl attackerClan = (ClanImpl) attackerModifiedPlayer.getClan();
        ClanImpl defenderClan = (ClanImpl) defenderModifiedPlayer.getClan();
        if (attackerClan == null || defenderClan == null) return;
        if (!attackerClan.getName().equals(defenderClan.getName())) return;
        if (attackerClan.isPvp()) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        if (event.getEntity() instanceof Player)
            return;
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) return;
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        final LevelManager levelManager = plugin.getClanManager().getLevelManager();
        clan.setXp(clan.getXp() + levelManager.getXpForMurder());
        int nextLevel = clan.getLevel() + 1;
        if (!(levelManager.getXpLevel(nextLevel) == -1)) {
            int xp = levelManager.getXpLevel(nextLevel);
            if (clan.getXp() >= xp) pluginManager.callEvent(new LevelUpEvent(clan));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan == null) return;
        Location location = event.getBlock().getLocation();
        Set<ProtectedRegion> protectedRegions = WorldGuard.getInstance().getPlatform().getRegionContainer().
                get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(BlockVector3.at(location.getBlockX(), location.getBlockY(),
                        location.getBlockZ())).getRegions().stream().filter(protectedRegion -> {
                    if (!protectedRegion.getFlags().containsKey(Flags.BLOCK_BREAK)) return false;
                    return protectedRegion.getFlags().get(Flags.BLOCK_BREAK) == StateFlag.State.DENY;
                }).collect(Collectors.toSet());
        if (!protectedRegions.isEmpty()) return;
        if (!Config.getSettings().whiteBlocks().contains(event.getBlock().getType())) return;
        final LevelManager levelManager = plugin.getClanManager().getLevelManager();
        clan.setXp(clan.getXp() + levelManager.getXpForMurder());
        int nextLevel = clan.getLevel() + 1;
        if (!(levelManager.getXpLevel(nextLevel) == -1)) {
            int xp = levelManager.getXpLevel(nextLevel);
            if (clan.getXp() >= xp) pluginManager.callEvent(new LevelUpEvent(clan));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();
        Player targetPlayer = event.getEntity();
        final StatsManager statsManager = plugin.getStatsManager();
        StatsManager.Stats targetPlayerStats = statsManager.getPlayers().get(targetPlayer.getUniqueId());
        if (player == null) {
            targetPlayerStats.setDeaths(targetPlayerStats.getDeaths() + 1);
            return;
        }
        StatsManager.Stats playerStats = statsManager.getPlayers().get(player.getUniqueId());
        targetPlayerStats.setDeaths(targetPlayerStats.getDeaths() + 1);
        playerStats.setKills(playerStats.getKills() + 1);
        ClanImpl clan = (ClanImpl) ModifiedPlayer.get(player).getClan();
        if (clan != null) {
            final LevelManager levelManager = plugin.getClanManager().getLevelManager();
            clan.setXp(clan.getXp() + levelManager.getXpForMurder());
            int nextLevel = clan.getLevel() + 1;
            if (!(levelManager.getXpLevel(nextLevel) == -1)) {
                int xp = levelManager.getXpLevel(nextLevel);
                if (clan.getXp() >= xp) pluginManager.callEvent(new LevelUpEvent(clan));
            }
        }
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GlowPacketListener.CACHE.remove(player.getEntityId());
        UUID playerUUID = player.getUniqueId();
        PlayerCalls.remove(playerUUID);
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan != null) {
            StorageManager storage = clan.getStorageManager();
            if (storage.getPlayers().contains(playerUUID)) storage.getPlayers().remove(playerUUID);
            if (storage.getIsUpdatedInventory().contains(playerUUID))
                storage.getIsUpdatedInventory().remove(playerUUID);
        }
        MenuClanMembersProvider menuClanMembersProvider = (MenuClanMembersProvider) MenuFactory.getProvider(MenuType.MENU_CLAN_MEMBERS);
        if (menuClanMembersProvider.isRegistered(player.getUniqueId()))
            menuClanMembersProvider.unRegister(player.getUniqueId());
        updateAllBossBarForPlayer(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) sendOperatorMessage(event.getPlayer());
        Player player = event.getPlayer();
        GlowPacketListener.CACHE.put(player.getEntityId(), player);
        UUID playerUUID = event.getPlayer().getUniqueId();
        final StatsManager statsManager = plugin.getStatsManager();
        if (!statsManager.getPlayers().containsKey(playerUUID)) {
            StatsManager.Stats stats = new StatsManager.Stats(0, 0);
            statsManager.getPlayers().put(playerUUID, stats);
        }
        updateAllBossBarForPlayer(event.getPlayer());
    }

    private void updateBossBarForPlayer(Player player, NamespacedKey key) {
        BossBar bossBar = plugin.getServer().getBossBar(key);
        if (!Event.isRunningEvent()) {
            if (bossBar != null) {
                if (bossBar.getPlayers().contains(player)) bossBar.removePlayer(player);
            }
        } else {
            if (bossBar != null) bossBar.addPlayer(player);
        }
    }

    private void updateAllBossBarForPlayer(Player player) {
        updateBossBarForPlayer(player, NamespacedKey.fromString("towerclans_bossbar_event_capture"));
    }

    private void sendOperatorMessage(Player player) {
        Utils.sendMessage(player, Utils.COLORIZER.colorize("&2[TowerClans] &7Telegram-канал разработчика: &d@tower_phr4se"));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Glow.changeForPlayer(ModifiedPlayer.get(event.getPlayer()), true);
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Glow.changeForPlayer(ModifiedPlayer.get(event.getPlayer()), true);
    }

    @EventHandler
    public void onCommandPreprocessCommand(PlayerCommandPreprocessEvent event) {
        if (!Event.isCurrentRunningEvent()) return;
        Event clanEvent = Event.getCurrentRunningEvent();
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");
        List<String> blockedCommands = configurationSection.getStringList("blocked_commands");
        String command = event.getMessage().split(" ")[0].replaceFirst("/", "");
        Player player = event.getPlayer();
        if (clanEvent.playerAtEvent(player) && blockedCommands.contains(command)) {
            Utils.sendMessage(player, Config.getMessages().useBlockedCommand().replace("%command%", command));
            event.setCancelled(true);
        }
    }
}

