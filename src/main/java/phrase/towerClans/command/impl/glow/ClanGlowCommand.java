package phrase.towerClans.command.impl.glow;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.util.ChatUtil;

public class ClanGlowCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;
    private ConfigurationSection section;

    public ClanGlowCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
        section = plugin.getConfig().getConfigurationSection("message.command.glow");
    }

    public void updateSection() {
        section = plugin.getConfig().getConfigurationSection("message.command.glow");
    }

    @Override
    public boolean handler(Player player, String[] args) {



        return false;
    }
}
