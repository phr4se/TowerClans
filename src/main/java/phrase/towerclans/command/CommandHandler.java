package phrase.towerclans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface CommandHandler {
    boolean handler(Player player, String[] args);
    default boolean handler(CommandSender commandSender, String[] args) {
        return true;
    }
}
