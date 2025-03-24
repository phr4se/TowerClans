package phrase.towerClans.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;

public class ChatUtil {

    private static String prefix;

    public ChatUtil(Plugin plugin) {
        prefix = plugin.getConfig().getString("message.prefix");
    }

    public void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(HexUtil.color(prefix + message));
    }

    public void sendMessage(Player player, String message) {
        if(player == null) return;
        player.sendMessage(HexUtil.color(prefix + message));

    }

}
