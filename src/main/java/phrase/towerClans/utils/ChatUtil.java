package phrase.towerClans.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;

public class ChatUtil {

    private static final String prefix = Plugin.getInstance().getConfig().getString("message.prefix");
    private static final ChatUtil chatUtil;

    static {
        chatUtil = new ChatUtil();
    }


    public void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(HexUtil.color(prefix + message));
    }

    public void sendMessage(Player player, String message) {
        if(player == null) return;
        player.sendMessage(HexUtil.color(prefix + message));

    }

    public static ChatUtil getChatUtil() {
        return chatUtil;
    }
}
