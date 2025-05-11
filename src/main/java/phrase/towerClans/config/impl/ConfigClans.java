package phrase.towerClans.config.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.impl.base.Base;
import phrase.towerClans.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigClans implements Config {

    private final File file;
    private final Plugin plugin;

    public ConfigClans(Plugin plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "clans.yml");
    }

    @Override
    public void save() {

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Ошибка при создании фалйа clans.yml");
                return;
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(String key : config.getKeys(false)) {
            if(!ClanImpl.getClans().containsKey(key)) config.set(key, null);
        }

        for(Map.Entry<String, ClanImpl> entry : ClanImpl.getClans().entrySet()) {
            String name = entry.getKey();
            ClanImpl clan = entry.getValue();

            if (config.contains(name + ".members")) {
                config.set(name + ".members", null);
            }


            config.set(name + ".level", clan.getLevel());
            config.set(name + ".xp", clan.getXp());
            config.set(name + ".balance", clan.getBalance());
            config.set(name + ".pvp", clan.isPvp());
            config.set(name + ".base", Base.getBase(clan));
            config.set(name + ".storage", clan.getStorage().getInventory().getContents());
            for (Map.Entry<ModifiedPlayer, String> entry2 : clan.getMembers().entrySet()) {

                String player = entry2.getKey().getPlayer().getName();
                String rank = entry2.getValue();

                if (!config.contains(name + ".members")) {
                    List<String> list = new ArrayList<>();
                    list.add(player + ":" + rank);
                    config.set(name + ".members", list);
                    continue;
                }

                List<String> list = config.getStringList(name + ".members");
                list.add(player + ":" + rank);

                config.set(name + ".members", list);

            }

        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить файл");
        }

    }

    @Override
    public void load() {

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Ошибка при создании фалйа clans.yml");
                return;
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(String key : config.getKeys(false)) {

            String name = key;
            int level = config.getInt(name + ".level");
            int xp = config.getInt(name + ".xp");
            int balance = config.getInt(name + ".balance");
            boolean pvp = config.getBoolean(name + ".pvp");
            Location base = config.getLocation(name + ".base");
            ItemStack[] contents = config.getList(name + ".storage").toArray(new ItemStack[0]);
            List<String> list = config.getStringList(name + ".members");
            Map<ModifiedPlayer, String> members = new HashMap<>();

            ClanImpl clan = new ClanImpl(key, plugin);
            clan.setLevel(level);
            clan.setXp(xp);
            clan.setBalance(balance);
            clan.setPvp(pvp);
            Base.setBase(clan, base);
            clan.getStorage().getInventory().setContents(contents);

            for(String string : list) {
                String[] strings = string.split(":");
                String player = strings[0];
                String rank = strings[1];
                ModifiedPlayer modifiedPlayer = new ModifiedPlayer(Bukkit.getOfflinePlayer(player).getUniqueId(), clan);

                members.put(modifiedPlayer, rank);

            }
            clan.setMembers(members);

            ClanImpl.getClans().put(name, clan);

        }


    }

}
