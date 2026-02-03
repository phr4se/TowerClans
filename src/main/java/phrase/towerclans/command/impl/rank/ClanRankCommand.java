package phrase.towerclans.command.impl.rank;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import phrase.towerclans.clan.ClanResponse;
import phrase.towerclans.clan.attribute.clan.RankType;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

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
        if (!clan.getMembers().get(modifiedPlayer).equals(RankType.LEADER.getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }
        String name = args[1];
        Player targetPlayer = Bukkit.getPlayer(name);
        if (targetPlayer == null) {
            Utils.sendMessage(player, Config.getCommandMessages().playerOffline());
            return true;
        }
        ModifiedPlayer targetModifierPlayer = ModifiedPlayer.get(targetPlayer);
        if (targetModifierPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().playerNotInClan());
            return true;
        }
        ClanImpl targetClan = (ClanImpl) targetModifierPlayer.getClan();
        if (!clan.getName().equals(targetClan.getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().playerNotInYourselfClan());
            return true;
        }
        int id;
        try {
            id = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return true;
        }
        if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            Utils.sendMessage(player, Config.getCommandMessages().notChangeRankYourself());
            return true;
        }
        ClanResponse clanResponse = clan.rank(targetModifierPlayer, id);
        if (clanResponse.isSuccess()) {
            String string = Config.getCommandMessages().giveRank().replace("%player%", targetPlayer.getName()).replace("%rank%", (id == 2) ? RankType.DEPUTY.getName() : RankType.MEMBER.getName());
            Utils.sendMessage(player, string);
            string = Config.getCommandMessages().givingRank().replace("%player%", player.getName()).replace("%rank%", (id == 2) ? RankType.DEPUTY.getName() : RankType.MEMBER.getName());
            Utils.sendMessage(targetPlayer, string);
            return true;
        } else {
            if (clanResponse.getMessage() != null) {
                Utils.sendMessage(player, clanResponse.getMessage());
            }
        }
        return false;
    }
}
