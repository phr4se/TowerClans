package phrase.towerClans.command.impl.reload;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.util.ChatUtil;

public class ClanReloadCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanReloadCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.reload");
        plugin.reloadConfig();
        chatUtil.sendMessage(player, configurationSection.getString("you_have_reloaded_the_config"));

        return true;
    }

}
