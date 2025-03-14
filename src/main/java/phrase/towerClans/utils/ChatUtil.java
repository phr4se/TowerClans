package phrase.towerClans.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;

public class ChatUtil {

    private final String prefix;

    public ChatUtil() {
        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message");
        prefix = configSection.getString("prefix");
    }

    public void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(HexUtil.color(prefix + message));
    }

    public void sendMessage(Player player, String message) {
        if(player == null) return;
        player.sendMessage(HexUtil.color(prefix + message));

    }

}
