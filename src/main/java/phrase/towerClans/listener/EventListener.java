package phrase.towerClans.listener;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.impls.invite.ClanInviteCommand;
import phrase.towerClans.utils.ChatUtil;
import phrase.towerClans.utils.HexUtil;

import java.util.Map;
import java.util.UUID;

public class EventListener implements Listener {

    private final ChatUtil chatUtil = new ChatUtil();

    public EventListener() {
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) return;

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 1), event.getInventory())) {

            if(event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }

            ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_clan.items.members_clan");

            if (event.getCurrentItem().getType() == Material.TOTEM_OF_UNDYING && event.getCurrentItem().getItemMeta().getDisplayName().
                    equalsIgnoreCase(HexUtil.color(configSection.getString("title")))) {
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, ClanImpl.MenuType.MENU_CLAN_MEMBERS.getId());
                return;
            }

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_clan.items.level_clan");

            if (event.getCurrentItem().getType() == Material.DIAMOND && event.getCurrentItem().getItemMeta().getDisplayName().
                    equalsIgnoreCase(HexUtil.color(configSection.getString("title")))) {
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, ClanImpl.MenuType.MENU_LEVEL_CLAN.getId());
                return;
            }

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_clan.items.exit");

            if(event.getCurrentItem().getType() == Material.SPECTRAL_ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(HexUtil.color(configSection.getString("title")))) {
                event.setCancelled(true);
                player.closeInventory();
                return;
            }

            event.setCancelled(true);

        }

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 2), event.getInventory())) {

            if(event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith("Участник")) {
                event.setCancelled(true);
                return;
            }

            ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_clan_members.in_menu");

            if(event.getCurrentItem().getType() == Material.SPECTRAL_ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(HexUtil.color(configSection.getString("title")))) {
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, ClanImpl.MenuType.MENU_CLAN.getId());
                return;
            }

            event.setCancelled(true);

        }

        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_level_clan.level.in_menu");

        if (ClanImpl.MenuType.identical(ClanImpl.MenuType.getMenu(clan, 3), event.getInventory())) {

            if(event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }

            if(event.getCurrentItem().getType() == Material.SPECTRAL_ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(HexUtil.color(configSection.getString("title")))) {
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, ClanImpl.MenuType.MENU_CLAN.getId());
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if(modifiedPlayer.getClan() == null) {
            event.setFormat(HexUtil.color(Plugin.getInstance().getConfig().getString("message.the_format_of_the_chat_message").replace("%clan_name%", "Нет").replace("%format%", event.getFormat())));
            return;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        event.setFormat(HexUtil.color(Plugin.getInstance().getConfig().getString("message.the_format_of_the_chat_message").replace("%clan_name%", clan.getName()).replace("%format%", event.getFormat())));
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {

        if(!(event.getDamager() instanceof Player)) return;

        Player attacker = (Player) event.getDamager();

        if(!(event.getEntity() instanceof Player)) return;

        Player defender = (Player) event.getEntity();

        ModifiedPlayer attackerModifiedPlayer = ModifiedPlayer.get(attacker);
        ModifiedPlayer defenderModifiedPlayer = ModifiedPlayer.get(defender);

        ClanImpl attackerClan = (ClanImpl) attackerModifiedPlayer.getClan();
        ClanImpl defenderClan = (ClanImpl) defenderModifiedPlayer.getClan();
        if(attackerClan == null || defenderClan == null) return;

        if(!attackerClan.getName().equals(defenderClan.getName())) return;

        if(attackerClan.isPvp()) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        Player player = event.getEntity().getKiller();
        if(player == null) return;

        if(event.getEntity() instanceof Player) return;

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if(modifiedPlayer.getClan() == null) return;
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        clan.setXp(clan.getXp() + 2);
        int level = clan.getLevel();
        int xp = AbstractClan.LevelType.getXpLevel(level);
        if(clan.getXp() >= xp) {
            for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
                ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message");
                String string = configSection.getString("notification_of_a_level_increase");
                chatUtil.sendMessage(entry.getKey().getPlayer(), string);
            }
            clan.setLevel(++level);
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();
        Player targetPlayer = event.getEntity();

        ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetPlayer);
        ClanImpl targetClan = (ClanImpl) targetModifiedPlayer.getClan();
        if(player == null) {
            if(targetClan == null) return;
            targetClan.setDeaths(targetClan.getDeaths() + 1);
            return;
        }

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if(clan == null) {
            if(targetClan == null) return;
            targetClan.setDeaths(targetClan.getDeaths() + 1);
            return;
        }
        if(targetClan == null) {
            clan.setKills(clan.getKills() + 1);
            return;
        }
        if(clan.getName().equals(targetClan.getName())) return;

        clan.setKills(clan.getKills() + 1);
        targetClan.setDeaths(targetClan.getDeaths() + 1);
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
        }.runTaskAsynchronously(Plugin.getInstance());
    }

}
