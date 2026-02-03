package phrase.towerclans.command.impl.chat;

import org.bukkit.entity.Player;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanChatCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }
        String message = Config.getCommandMessages().messageFormat().replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", clan.getMembers().get(modifiedPlayer)).replace("%message%", stringBuilder.toString());
        clan.chat(message);
        return true;
    }
}
