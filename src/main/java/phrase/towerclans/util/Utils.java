package phrase.towerclans.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phrase.towerclans.util.colorizer.ColorizerProvider;

import java.util.regex.Pattern;

public class Utils {
    public static final Pattern PATTERN = Pattern.compile("%(.*?)%");
    public static ColorizerProvider colorizer;

    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(message);
    }

    public static void sendMessage(Player player, String message) {
        if (player == null || message == null) return;
        player.sendMessage(message);
    }
}
