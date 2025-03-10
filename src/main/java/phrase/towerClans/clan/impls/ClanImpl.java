package phrase.towerClans.clan.impls;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.utils.ChatUtil;
import phrase.towerClans.utils.HexUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanImpl extends AbstractClan implements Listener {

    public static Map<String, ClanImpl> clans = new HashMap<>();

    public static final File file = new File(Plugin.instance.getDataFolder(), "clans.yml");
    public static final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    public ClanImpl() {
    }

    public ClanImpl(String name) {
        super(name);
    }

    @Override
    public boolean invite(ModifiedPlayer modifiedPlayer) {
        if (getMembers().containsKey(modifiedPlayer)) return false;
        getMembers().put(modifiedPlayer, RankType.MEMBER.getName());

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = Plugin.instance.getConfig().getString("message.command.invite.notification_of_the_invitation").replace("%player%", modifiedPlayer.getPlayer().getName());
            ChatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean kick(ModifiedPlayer modifiedPlayer) {
        if(!getMembers().containsKey(modifiedPlayer)) return false;
        getMembers().remove(modifiedPlayer);

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = Plugin.instance.getConfig().getString("message.command.invite.notification_of_exclusion").replace("%player%", modifiedPlayer.getPlayer().getName());
            ChatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean invest(ModifiedPlayer modifiedPlayer, int amount) {
        if(Plugin.instance.economy.getBalance(modifiedPlayer.getPlayer()) < amount) return false;
        int maximumBalance = LevelType.getLevelMaximumBalance(getLevel());
        if((getBalance() + amount) > maximumBalance) return false;
        Plugin.instance.economy.withdrawPlayer(modifiedPlayer.getPlayer(), amount);
        setBalance(getBalance() + amount);

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = Plugin.instance.getConfig().getString("message.command.invest.notification_of_investment").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            ChatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean withdraw(ModifiedPlayer modifiedPlayer, int amount) {
        if(getBalance() < amount) return false;
        Plugin.instance.economy.depositPlayer(modifiedPlayer.getPlayer(), amount);
        setBalance(getBalance() - amount);

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = Plugin.instance.getConfig().getString("message.command.withdraw.notification_of_withdrawal").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            ChatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean leave(ModifiedPlayer modifiedPlayer) {
        if(!getMembers().containsKey(modifiedPlayer)) return false;
        getMembers().remove(modifiedPlayer);

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = Plugin.instance.getConfig().getString("message.command.leave.notification_of_exclusion").replace("%player%", modifiedPlayer.getPlayer().getName());
            ChatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean rank(ModifiedPlayer modifiedPlayer, int id) {
        if(id == 1) return false;

        if(id == 2) getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), RankType.DEPUTY.getName());
        else if(id == 3) getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), RankType.MEMBER.getName());
        else return false;

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = Plugin.instance.getConfig().getString("message.command.rank.notification_of_rank").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", (id == 2) ? RankType.DEPUTY.getName() : RankType.MEMBER.getName());
            ChatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    public static void loadData() {

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Plugin.instance.getLogger().severe("Ошибка при создании фалйа clans.yml");
                return;
            }
        }

        for(String key : config.getKeys(false)) {

            String name = key;
            int level = config.getInt(name + ".level");
            int xp = config.getInt(name + ".xp");
            int balance = config.getInt(name + ".balance");
            boolean pvp = config.getBoolean(name + ".pvp");
            List<String> list = config.getStringList(name + ".members");
            Map<ModifiedPlayer, String> members = new HashMap<>();

            ClanImpl clan = new ClanImpl();
            clan.setName(key);
            clan.setLevel(level);
            clan.setXp(xp);
            clan.setBalance(balance);
            clan.setPvp(pvp);


            for(String string : list) {
                String[] strings = string.split(":");
                String player = strings[0];
                String rank = strings[1];
                System.out.println(player + ":" + rank);
                ModifiedPlayer modifiedPlayer = new ModifiedPlayer(Bukkit.getOfflinePlayer(player).getUniqueId(), clan);

                members.put(modifiedPlayer, rank);

            }
            clan.setMembers(members);

            clans.put(name, clan);

        }

    }

    public static void saveData() {

        for(Map.Entry<String, ClanImpl> entry : clans.entrySet()) {
            String name = entry.getKey();
            ClanImpl clan = entry.getValue();

            if (config.contains(name + ".members")) {
                config.set(name = ".members", null);
            }


            config.set(name + ".level", clan.getLevel());
            config.set(name + ".xp", clan.getXp());
            config.set(name + ".balance", clan.getBalance());
            config.set(name + ".pvp", clan.isPvp());
            for (Map.Entry<ModifiedPlayer, String> entry2 : clan.getMembers().entrySet()) {

                String player = entry2.getKey().getPlayer().getName();
                String rank = entry2.getValue();

                if (!config.contains(name + ".members")) {
                    List<String> list = new ArrayList<>();
                    list.add(player + ":" + rank);
                }

                List<String> list = config.getStringList(name + ".members");
                list.add(player + ":" + rank);

                config.set(name + ".members", list);
            }

            try {
                config.save(file);
            } catch (IOException e) {
                Plugin.instance.getLogger().severe("Не удалось сохранить файл");
            }

        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) {
            event.setCancelled(true);
            return;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (event.getView().getTitle().startsWith("Клан")) {

            if(event.getCurrentItem() == null) return;

            if (event.getCurrentItem().getType() == Material.TOTEM_OF_UNDYING && event.getCurrentItem().getItemMeta().getDisplayName().equals("Участники клана")) {
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, MenuType.MENU_CLAN_MEMBERS.getId());
                return;
            }

            if (event.getCurrentItem().getType() == Material.DIAMOND && event.getCurrentItem().getItemMeta().getDisplayName().equals("Уровень клана")) {
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, MenuType.MENU_LEVEL_CLAN.getId());
                return;
            }

            if(event.getCurrentItem().getType() == Material.SPECTRAL_ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equals(HexUtil.color(Plugin.instance.getConfig().getString("settings.menu.menu_clan.items.exit.title")))) {
                event.setCancelled(true);
                player.closeInventory();
            }

            event.setCancelled(true);

        }

        if (event.getView().getTitle().equals("Участники клана")) {

            if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith("Участник")) {
                event.setCancelled(true);
                return;
            }

            if(event.getCurrentItem().getType() == Material.SPECTRAL_ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equals("В меню")) {
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, MenuType.MENU_CLAN.getId());
            }

            event.setCancelled(true);

        }

        if (event.getView().getTitle().equals("Уровень клана")) {
            event.setCancelled(true);
            if(event.getCurrentItem().getType() == Material.SPECTRAL_ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equals("В меню")) {
                event.setCancelled(true);
                clan.showMenu(modifiedPlayer, MenuType.MENU_CLAN.getId());
            }
        }
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if(modifiedPlayer.getClan() == null) {
            event.setFormat(HexUtil.color(Plugin.instance.getConfig().getString("message.the_format_of_the_chat_message").replace("%clan_name%", "Нет").replace("%format%", event.getFormat())));
            return;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        event.setFormat(HexUtil.color(Plugin.instance.getConfig().getString("message.the_format_of_the_chat_message").replace("%clan_name%", clan.getName()).replace("%format%", event.getFormat())));
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

        if(!attackerClan.getName().equals(defenderClan.getName())) return;

        if(attackerClan.isPvp()) event.setCancelled(true);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {

        if(event.getEntity() instanceof LivingEntity && event.getEntity().getKiller() instanceof Player) {

            Player player = event.getEntity().getKiller();
            ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
            if(modifiedPlayer.getClan() == null) return;

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
            clan.setXp(clan.getXp() + 2);
            int level = clan.getLevel();
            int xp = LevelType.getXpLevel(level);
            if(clan.getXp() >= xp) {
                for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
                    String string = Plugin.instance.getConfig().getString("message.notification_of_a_level_increase");
                    ChatUtil.sendMessage(entry.getKey().getPlayer(), string);
                }
                clan.setLevel(++level);
            }
        }

    }

}
