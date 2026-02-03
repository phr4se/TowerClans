package phrase.towerclans.command.impl.invite;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

import java.util.UUID;

public class ClanCancelCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        UUID senderPlayer = PlayerCalls.removePlayer(player.getUniqueId());
        if (senderPlayer == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInvited());
            return true;
        }
        Utils.sendMessage(player, Config.getCommandMessages().declineInvited());
        Utils.sendMessage(Bukkit.getPlayer(senderPlayer), Config.getCommandMessages().playerDeclineInvited());
        return true;
    }
}
