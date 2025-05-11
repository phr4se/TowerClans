package phrase.towerClans.config.impl;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ConfigPlayers implements Config {

    private final File file;
    private final Plugin plugin;

    public ConfigPlayers(Plugin plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "players.yml");
    }

    @Override
    public void save() {

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Ошибка при создании фалйа players.yml");
                return;
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(Map.Entry<UUID, Stats> entry : Stats.PLAYERS.entrySet()) {

            Player player = Bukkit.getPlayer(entry.getKey());
            Stats playerStats = entry.getValue();

            if(player == null) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
                config.set(offlinePlayer.getName() + ".kills", playerStats.getKills());
                config.set(offlinePlayer.getName() + ".deaths", playerStats.getDeaths());

                try {
                    config.save(file);
                } catch (IOException e) {
                    plugin.getLogger().severe("Не удалось сохранить файл");
                }

                return;

            }

            config.set(player.getName() + ".kills", playerStats.getKills());
            config.set(player.getName() + ".deaths", playerStats.getDeaths());

            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось сохранить файл");
            }

        }
    }

    @Override
    public void load() {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Ошибка при создании фалйа players.yml");
                return;
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(String key : config.getKeys(false)) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(key);
            int kills = config.getInt(key + ".kills");
            int deaths = config.getInt(key + ".deaths");

            Stats.PLAYERS.put(offlinePlayer.getUniqueId(), new Stats(kills, deaths));

        }
    }

}
