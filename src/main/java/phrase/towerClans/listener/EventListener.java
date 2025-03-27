package phrase.towerClans.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.Level;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.PlayerStats;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.impls.invite.ClanInviteCommand;
import phrase.towerClans.utils.ChatUtil;
import phrase.towerClans.utils.HexUtil;

import java.util.Map;
import java.util.UUID;

public class EventListener implements Listener {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public EventListener(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) return;

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 1, plugin), event.getInventory())) {

            if(event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }

            ItemStack item = event.getCurrentItem();
            if(item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING) != null) {
                String action = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING);
                ClanImpl.MenuType menu = ClanImpl.MenuType.valueOf(action);
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, menu.getId());
                return;
            } else {
                event.setCancelled(true);
                return;
            }



        }

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 2, plugin), event.getInventory())) {

            if(event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }

            ItemStack item = event.getCurrentItem();
            if((item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING)) != null) {
                String action = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING);

                ClanImpl.MenuType menu = ClanImpl.MenuType.valueOf(action);
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, menu.getId());
            } else {
                event.setCancelled(true);
                return;
            }

        }

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 3, plugin), event.getInventory())) {

            if(event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }

            ItemStack item = event.getCurrentItem();
            if((item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING)) != null) {
                String action = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("action"), PersistentDataType.STRING);

                ClanImpl.MenuType menu = ClanImpl.MenuType.valueOf(action);
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, menu.getId());
            } else {
                event.setCancelled(true);
            }

        }

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if(modifiedPlayer.getClan() == null) {
            event.setFormat(HexUtil.color(plugin.getConfig().getString("message.the_format_of_the_chat_message").replace("%clan_name%", "Нет").replace("%format%", event.getFormat())));
            return;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        event.setFormat(HexUtil.color(plugin.getConfig().getString("message.the_format_of_the_chat_message").replace("%clan_name%", clan.getName()).replace("%format%", event.getFormat())));
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

                clan.setXp(clan.getXp() + 2);
                int level = clan.getLevel();
                int xp = Level.getXpLevel(level);
                if(clan.getXp() >= xp) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
                                ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("message");
                                String string = configSection.getString("notification_of_a_level_increase");
                                chatUtil.sendMessage(entry.getKey().getPlayer(), string);
                            }
                            cancel();
                        }
                    }.runTask(plugin);
                    clan.setLevel(++level);
                }

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

                PlayerStats targetPlayerStats = PlayerStats.PLAYERS.get(targetPlayer.getUniqueId());
                if(player == null) {
                    targetPlayerStats.setDeaths(targetPlayerStats.getDeaths() + 1);
                    cancel();
                    return;
                }
                PlayerStats playerStats = PlayerStats.PLAYERS.get(player.getUniqueId());
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
                UUID player = event.getPlayer().getUniqueId();
                for (Map.Entry<UUID, UUID> entry : ClanInviteCommand.PLAYERS.entrySet()) {

                    if(!entry.getKey().equals(player) && !entry.getValue().equals(player)) continue;

                    ClanInviteCommand.PLAYERS.remove(entry.getKey());

                }
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

                if(PlayerStats.PLAYERS.containsKey(player)) {
                    cancel();
                    return;
                }

                PlayerStats.PLAYERS.put(player, new PlayerStats(0, 0));
                cancel();
            }
        }.runTaskAsynchronously(plugin);

    }

}
