package phrase.towerClans.command.impl.info;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

import java.util.List;

public class ClanInfoCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {


        if(args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }

        String name = args[1];

        ClanImpl clan = ClanImpl.getClans().get(name);
        if(clan == null) {
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
                    .replace("%kills%", String.valueOf(Stats.getKillsMembers(clan.getMembers())))
                    .replace("%deaths%", String.valueOf(Stats.getDeathsMembers(clan.getMembers())));
            return Utils.COLORIZER.colorize(replacedString);
        }).toList();

        for(String string : replacedList) {
            Utils.sendMessage(player, string);
        }

        return true;
    }
}
