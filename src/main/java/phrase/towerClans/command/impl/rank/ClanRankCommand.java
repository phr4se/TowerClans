package phrase.towerClans.command.impl.rank;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ClanResponse;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

public class ClanRankCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (args.length < 3) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }

        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (!clan.getMembers().get(modifiedPlayer).equals(Rank.RankType.LEADER.getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }

        String name = args[1];
        Player targetPlayer;
        try {
            targetPlayer = Bukkit.getPlayer(name);
        } catch (NullPointerException e) {
            Utils.sendMessage(player, Config.getCommandMessages().playerOffline());
            return true;
        }

        ModifiedPlayer targetModifierPlayer = ModifiedPlayer.get(targetPlayer);

        int id;
        try {
            id = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return true;
        }

        if(player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            Utils.sendMessage(player, Config.getCommandMessages().notChangeRankYourself());
            return true;
        }

        ClanResponse clanResponse = clan.rank(targetModifierPlayer, id);

        if (clanResponse.isSuccess()) {
            String string = Config.getCommandMessages().giveRank().replace("%player%", targetPlayer.getName()).replace("%rank%", (id == 2) ? Rank.RankType.DEPUTY.getName() : Rank.RankType.MEMBER.getName());
            Utils.sendMessage(player, string);
            string = Config.getCommandMessages().givingRank().replace("%player%", player.getName()).replace("%rank%", (id == 2) ? Rank.RankType.DEPUTY.getName() : Rank.RankType.MEMBER.getName());
            Utils.sendMessage(targetPlayer, string);
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                Utils.sendMessage(player, clanResponse.getMessage());
            }
        }

        return false;
    }
}
