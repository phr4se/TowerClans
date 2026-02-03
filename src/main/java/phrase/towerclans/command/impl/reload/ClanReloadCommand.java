package phrase.towerclans.command.impl.reload;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import phrase.towerclans.Plugin;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanReloadCommand implements CommandHandler {
    private final Plugin plugin;

    public ClanReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        plugin.getServer().getPluginManager().enablePlugin(plugin);
        Utils.sendMessage(player, Config.getCommandMessages().reloadConfig());
        return true;
    }
}
