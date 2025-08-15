package phrase.towerClans.command.impl.withdraw;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ClanResponse;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.clan.permission.PermissionType;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

public class ClanWithdrawCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if(!modifiedPlayer.hasPermission(PermissionType.WITHDRAW)) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }

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

        ClanResponse clanResponse = clan.withdraw(modifiedPlayer, amount);
        if (clanResponse.isSuccess()) {
            Utils.sendMessage(player, Config.getCommandMessages().withdrawWithClan());
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                Utils.sendMessage(player, clanResponse.getMessage());
            }
        }

        return true;
    }
}
