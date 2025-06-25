package phrase.towerClans.command.impl.reload;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

public class ClanReloadCommand implements CommandHandler {

    private final Plugin plugin;

    public ClanReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        plugin.reloadConfig();
        Config.setupSettings(plugin.getConfig());
        Config.setupMessages(YamlConfiguration.loadConfiguration(plugin.getMessagesFile()));
        Utils.sendMessage(player, Config.getCommandMessages().reloadConfig());
        return true;
    }

}
