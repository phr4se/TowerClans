package phrase.towerClans.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.projectiles.ProjectileSource;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.clan.attribute.clan.Storage;
import phrase.towerClans.clan.event.Event;
import phrase.towerClans.clan.event.impl.Capture;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.impl.invite.PlayerCalls;
import phrase.towerClans.config.Config;
import phrase.towerClans.event.*;
import phrase.towerClans.glow.Glow;
import phrase.towerClans.gui.MenuFactory;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.gui.MenuType;
import phrase.towerClans.gui.impl.MenuClanMembersProvider;
import phrase.towerClans.util.Utils;

import java.util.*;

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

        if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_MAIN).getMenu(modifiedPlayer, clan, plugin), event.getInventory())) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanMainEvent(modifiedPlayer, event));
        }

        if (((MenuClanMembersProvider) MenuFactory.getProvider(MenuType.MENU_CLAN_MEMBERS)).getMenuPages(player.getUniqueId()) != null) {
            MenuPages menuPages = ((MenuClanMembersProvider) MenuFactory.getProvider(MenuType.MENU_CLAN_MEMBERS)).getMenuPages(player.getUniqueId());
            if (identical(menuPages.getPage(menuPages.getCurrentPage()), event.getInventory())) {
                if (!event.isLeftClick() && !event.isRightClick()) {
                    event.setCancelled(true);
                    return;
                }
                pluginManager.callEvent(new ClickMenuClanMembersEvent(modifiedPlayer, event));
            }
        }

        if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_MEMBERS).getMenu(modifiedPlayer, clan, plugin), event.getInventory())) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanMembersEvent(modifiedPlayer, event));
        }

        if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_LEVEL).getMenu(modifiedPlayer, clan, plugin), event.getInventory())) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanLevelEvent(modifiedPlayer, event));
        }

        if (identical(MenuFactory.getProvider(MenuType.MENU_CLAN_STORAGE).getMenu(modifiedPlayer, clan, plugin), event.getInventory())) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanStorageEvent(clan, player, event.getInventory(), event));
        }

        if(identical(MenuFactory.getProvider(MenuType.MENU_CLAN_GLOW).getMenu(modifiedPlayer, clan, plugin), event.getInventory())) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                event.setCancelled(true);
                return;
            }
            pluginManager.callEvent(new ClickMenuClanGlowEvent(modifiedPlayer, event));
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

            if (!item1.isSimilar(item2)) return false;

        }

        return true;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan != null) {
            Storage storage = clan.getStorage();
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

        if(event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if(((Projectile) event.getDamager()).getShooter() instanceof Player) {
                attacker = (Player) ((Projectile) event.getDamager()).getShooter();
            }
        }

        if(attacker == null) return;

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
        clan.setXp(clan.getXp() + Level.getXpForMurder());

        int nextLevel = clan.getLevel() + 1;
        if(!(Level.getXpLevel(nextLevel) == -1)) {
            int xp = Level.getXpLevel(nextLevel);
            if (clan.getXp() >= xp) pluginManager.callEvent(new LevelUpEvent(clan));
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity().getKiller();
        Player targetPlayer = event.getEntity();

        Stats targetPlayerStats = Stats.PLAYERS.get(targetPlayer.getUniqueId());
        if (player == null) {
            targetPlayerStats.setDeaths(targetPlayerStats.getDeaths() + 1);
            return;
        }
        Stats playerStats = Stats.PLAYERS.get(player.getUniqueId());
        targetPlayerStats.setDeaths(targetPlayerStats.getDeaths() + 1);
        playerStats.setKills(playerStats.getKills() + 1);
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        PlayerCalls.remove(playerUUID);

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan != null) {
            Storage storage = clan.getStorage();
            if (storage.getPlayers().contains(playerUUID)) storage.getPlayers().remove(playerUUID);
            if (storage.getIsUpdatedInventory().contains(playerUUID))
                storage.getIsUpdatedInventory().remove(playerUUID);
        }

        MenuClanMembersProvider menuClanMembersProvider = (MenuClanMembersProvider) MenuFactory.getProvider(MenuType.MENU_CLAN_MEMBERS);
        if (menuClanMembersProvider.isRegistered(player.getUniqueId())) menuClanMembersProvider.unRegister(player.getUniqueId());

        BossBar bossBar = plugin.getServer().getBossBar(NamespacedKey.fromString("towerclans_bossbar_event_capture"));
        if(bossBar != null) {
            if(bossBar.getPlayers().contains(player)) bossBar.removePlayer(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if(event.getPlayer().isOp()) sendOperatorMessage(event.getPlayer());

        UUID player = event.getPlayer().getUniqueId();

        if (!Stats.PLAYERS.containsKey(player)) {
            Stats stats = new Stats(0, 0);
            Stats.PLAYERS.put(player, stats);
        }

        BossBar bossBar = plugin.getServer().getBossBar(NamespacedKey.fromString("towerclans_bossbar_event_capture"));
        if(!Event.isRunningEvent()) {
            if(bossBar != null)
                if(bossBar.getPlayers().contains(player)) bossBar.removePlayer(event.getPlayer());
        } else {
            if(bossBar != null) bossBar.addPlayer(event.getPlayer());
        }
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

        if(!Event.isRunningEventType(Event.EventType.CAPTURE)) return;

        Capture capture = (Capture) Event.getRunningEvent(Event.EventType.CAPTURE);

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");
        List<String> blockedCommands = configurationSection.getStringList("blocked_commands");

        String command = event.getMessage().split(" ")[0].replaceFirst("/", "");
        Player player = event.getPlayer();
        if(capture.playerAtEvent(player) && blockedCommands.contains(command)) {
            Utils.sendMessage(player, Config.getMessages().useBlockedCommand().replace("%command%", command));
            event.setCancelled(true);
        }

    }

}

