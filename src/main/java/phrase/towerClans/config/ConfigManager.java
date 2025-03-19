package phrase.towerClans.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private static File file;
    private static YamlConfiguration config;

    public ConfigManager() {
        file = new File(Plugin.getInstance().getDataFolder(), "clans.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void loadData() {

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Plugin.getInstance().getLogger().severe("Ошибка при создании фалйа clans.yml");
                return;
            }
        }

        for(String key : config.getKeys(false)) {

            String name = key;
            int level = config.getInt(name + ".level");
            int xp = config.getInt(name + ".xp");
            int balance = config.getInt(name + ".balance");
            boolean pvp = config.getBoolean(name + ".pvp");
            int kills = config.getInt(name + ".kills");
            int deaths = config.getInt(name + ".deaths");
            List<String> list = config.getStringList(name + ".members");
            Map<ModifiedPlayer, String> members = new HashMap<>();

            ClanImpl clan = new ClanImpl();
            clan.setName(key);
            clan.setLevel(level);
            clan.setXp(xp);
            clan.setBalance(balance);
            clan.setPvp(pvp);
            clan.setKills(kills);
            clan.setDeaths(deaths);


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

    public void saveData() {

        for(Map.Entry<String, ClanImpl> entry : ClanImpl.getClans().entrySet()) {
            String name = entry.getKey();
            ClanImpl clan = entry.getValue();

            if (config.contains(name + ".members")) {
                config.set(name = ".members", null);
            }


            config.set(name + ".level", clan.getLevel());
            config.set(name + ".xp", clan.getXp());
            config.set(name + ".balance", clan.getBalance());
            config.set(name + ".pvp", clan.isPvp());
            config.set(name + ".kills", clan.getKills());
            config.set(name + ".deaths", clan.getDeaths());
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
                Plugin.getInstance().getLogger().severe("Не удалось сохранить файл");
            }

        }

    }

}
