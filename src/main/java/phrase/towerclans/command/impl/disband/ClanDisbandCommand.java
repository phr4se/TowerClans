package phrase.towerclans.command.impl.disband;

import org.bukkit.entity.Player;
import phrase.towerclans.clan.ClanResponse;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanDisbandCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        ClanResponse clanResponse = clan.disband(modifiedPlayer);
        if (clanResponse.isSuccess()) {
            Utils.sendMessage(player, Config.getCommandMessages().deleteClan());
            return true;
        } else {
            if (clanResponse.getMessage() != null) {
                Utils.sendMessage(player, clanResponse.getMessage());
            }
        }
        return true;
    }
}
