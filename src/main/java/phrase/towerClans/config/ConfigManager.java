package phrase.towerClans.config;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.PlayerStats;
import phrase.towerClans.clan.impls.ClanImpl;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private static File fileClans;
    private static YamlConfiguration configClans;
    private static File filePlayers;
    private static YamlConfiguration configPlayers;

    public ConfigManager() {
        fileClans = new File(Plugin.getInstance().getDataFolder(), "clans.yml");
        configClans = YamlConfiguration.loadConfiguration(fileClans);
        filePlayers = new File(Plugin.getInstance().getDataFolder(), "players.yml");
        configPlayers = YamlConfiguration.loadConfiguration(filePlayers);
    }

    public void loadClans() {

        if(!fileClans.exists()) {
            try {
                fileClans.createNewFile();
            } catch (IOException e) {
                Plugin.getInstance().getLogger().severe("Ошибка при создании фалйа clans.yml");
                return;
            }
        }

        for(String key : configClans.getKeys(false)) {

            String name = key;
            int level = configClans.getInt(name + ".level");
            int xp = configClans.getInt(name + ".xp");
            int balance = configClans.getInt(name + ".balance");
            boolean pvp = configClans.getBoolean(name + ".pvp");
            List<String> list = configClans.getStringList(name + ".members");
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

            ClanImpl.getClans().put(name, clan);

        }

    }

    public void loadPlayers() {
        if(!filePlayers.exists()) {
            try {
                filePlayers.createNewFile();
            } catch (IOException e) {
                Plugin.getInstance().getLogger().severe("Ошибка при создании фалйа clans.yml");
                return;
            }
        }

        for(String key : configPlayers.getKeys(false)) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(key);
            int kills = configPlayers.getInt(offlinePlayer.getName() + ".kills");
            int deaths = configPlayers.getInt(offlinePlayer.getName() + ".deaths");

            PlayerStats.PLAYERS.put(offlinePlayer.getUniqueId(), new PlayerStats(kills, deaths));

        }
    }

    public void saveClans() {

        for(Map.Entry<String, ClanImpl> entry : ClanImpl.getClans().entrySet()) {
            String name = entry.getKey();
            ClanImpl clan = entry.getValue();

            if (configClans.contains(name + ".members")) {
                configClans.set(name = ".members", null);
            }


            configClans.set(name + ".level", clan.getLevel());
            configClans.set(name + ".xp", clan.getXp());
            configClans.set(name + ".balance", clan.getBalance());
            configClans.set(name + ".pvp", clan.isPvp());
            for (Map.Entry<ModifiedPlayer, String> entry2 : clan.getMembers().entrySet()) {

                String player = entry2.getKey().getPlayer().getName();
                String rank = entry2.getValue();

                if (!configClans.contains(name + ".members")) {
                    List<String> list = new ArrayList<>();
                    list.add(player + ":" + rank);
                }

                List<String> list = configClans.getStringList(name + ".members");
                list.add(player + ":" + rank);

                configClans.set(name + ".members", list);
            }

            try {
                configClans.save(fileClans);
            } catch (IOException e) {
                Plugin.getInstance().getLogger().severe("Не удалось сохранить файл");
            }

        }

    }

    public void savePlayers() {

        for(Map.Entry<UUID, PlayerStats> entry : PlayerStats.PLAYERS.entrySet()) {

            Player player = Bukkit.getPlayer(entry.getKey());
            PlayerStats playerStats = entry.getValue();

            if(player == null) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
                configPlayers.set(offlinePlayer.getName() + ".kills", playerStats.getKills());
                configPlayers.set(offlinePlayer.getName() + ".deaths", playerStats.getDeaths());

                try {
                    configPlayers.save(filePlayers);
                } catch (IOException e) {
                    Plugin.getInstance().getLogger().severe("Не удалось сохранить файл");
                }

                return;

            }

            configPlayers.set(player.getName() + ".kills", playerStats.getKills());
            configPlayers.set(player.getName() + ".deaths", playerStats.getDeaths());

            try {
                configPlayers.save(filePlayers);
            } catch (IOException e) {
                Plugin.getInstance().getLogger().severe("Не удалось сохранить файл");
            }

        }

    }

}
