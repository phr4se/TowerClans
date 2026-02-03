package phrase.towerclans.command.impl.invest;

import org.bukkit.entity.Player;
import phrase.towerclans.clan.ClanResponse;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanInvestCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }
        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }
        ClanResponse clanResponse = clan.invest(modifiedPlayer, amount);
        if (clanResponse.isSuccess()) {
            Utils.sendMessage(player, Config.getCommandMessages().putInClan());
            return true;
        } else {
            if (clanResponse.getMessage() != null) {
                Utils.sendMessage(player, clanResponse.getMessage());
            }
        }
        return true;
    }
}
