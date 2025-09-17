package phrase.towerClans.command.impl.stats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

import java.util.List;

public class ClanStatsCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if(clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }

        if (args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);

        ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetPlayer);

        if(targetModifiedPlayer == null) {
            Utils.sendMessage(player, Config.getCommandMessages().playerOffline());
            return true;
        }

        ClanImpl targetClan = (ClanImpl) targetModifiedPlayer.getClan();

        if (targetClan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().playerNotInClan());
            return true;
        }

        if (!clan.getName().equals(targetClan.getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().playerNotInYourselfClan());
            return true;
        }

        Stats playerStats = Stats.PLAYERS.get(targetPlayer.getUniqueId());
        int kills = playerStats.getKills();
        int deaths = playerStats.getDeaths();

        List<String> list = Config.getCommandMessages().statisticPlayer();

        for (String string : list) {
            Utils.sendMessage(player, string.replace("%kills%", String.valueOf(kills)).replace("%deaths%", String.valueOf(deaths)).replace("%player%", targetPlayer.getName()));
        }

        return true;
    }

}
