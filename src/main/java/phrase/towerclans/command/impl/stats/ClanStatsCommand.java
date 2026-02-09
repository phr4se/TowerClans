package phrase.towerclans.command.impl.stats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.attribute.player.StatsManager;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

import java.util.List;

public class ClanStatsCommand implements CommandHandler {
    private final TowerClans plugin;

    public ClanStatsCommand(TowerClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        if (args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetPlayer);
        if (targetModifiedPlayer == null) {
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
        StatsManager.Stats playerStats = plugin.getStatsManager().getPlayers().get(targetPlayer.getUniqueId());
        int kills = playerStats.getKills();
        int deaths = playerStats.getDeaths();
        List<String> list = Config.getCommandMessages().statisticPlayer();
        for (String string : list) {
            Utils.sendMessage(player, string.replace("%kills%", String.valueOf(kills)).replace("%deaths%", String.valueOf(deaths)).replace("%player%", targetPlayer.getName()));
        }
        return true;
    }
}
