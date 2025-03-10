package phrase.towerClans.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;

public class ChatUtil {

    private static final String prefix = Plugin.instance.getConfig().getString("message.prefix");

    private ChatUtil() {
    }

    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(HexUtil.color(prefix + message));
    }

    public static void sendMessage(Player player, String message) {
        if(player == null) return;
        player.sendMessage( HexUtil.color(prefix + message));

    }

}
