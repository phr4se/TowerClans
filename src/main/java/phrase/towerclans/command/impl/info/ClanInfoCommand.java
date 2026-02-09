package phrase.towerclans.command.impl.info;

import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

import java.util.List;

public class ClanInfoCommand implements CommandHandler {
    private final TowerClans plugin;

    public ClanInfoCommand(TowerClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        if (args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }
        String name = args[1];
        ClanImpl clan = plugin.getClanManager().getClan(name);
        if (clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().clanNoExists());
            return true;
        }
        List<String> list = Config.getCommandMessages().informationClan();
        List<String> replacedList = list.stream().map(string -> {
            String replacedString = string
                    .replace("%name%", clan.getName())
                    .replace("%members%", String.valueOf(clan.getMembers().size()))
                    .replace("%level%", String.valueOf(clan.getLevel()))
                    .replace("%xp%", String.valueOf(clan.getXp()))
                    .replace("%balance%", String.valueOf(clan.getBalance()))
                    .replace("%kills%", String.valueOf(plugin.getStatsManager().getKillsMembers(clan.getMembers())))
                    .replace("%deaths%", String.valueOf(plugin.getStatsManager().getDeathsMembers(clan.getMembers())));
            return Utils.COLORIZER.colorize(replacedString);
        }).toList();
        for (String string : replacedList) {
            Utils.sendMessage(player, string);
        }
        return true;
    }
}
