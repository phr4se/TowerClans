package phrase.towerClans.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.colorizer.ColorizerFactory;
import phrase.towerClans.util.colorizer.ColorizerProvider;
import phrase.towerClans.util.colorizer.ColorizerType;

public class Utils {

    public static final ColorizerProvider COLORIZER;

    static {
        COLORIZER = ColorizerFactory.getProvider(ColorizerType.HEX);
    }

    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(message);
    }

    public static void sendMessage(Player player, String message) {
        if(player == null || message == null) return;
        player.sendMessage(message);
    }

}
