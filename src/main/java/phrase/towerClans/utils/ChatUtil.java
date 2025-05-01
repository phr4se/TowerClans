package phrase.towerClans.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.utils.colorizer.ColorizerProvider;

public class ChatUtil {

    private static String prefix;
    private final static ColorizerProvider colorizerProvider;

    static {
        colorizerProvider = Plugin.getColorizerProvider();
    }

    public ChatUtil(Plugin plugin) {
        prefix = plugin.getConfig().getString("message.prefix");
    }

    public void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(colorizerProvider.colorize(prefix + message));
    }

    public void sendMessage(Player player, String message) {
        if(player == null) return;
        player.sendMessage(colorizerProvider.colorize(prefix + message));
    }

}
