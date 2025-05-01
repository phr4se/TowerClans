package phrase.towerClans.commands.impl.info;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attributes.player.Stats;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;
import phrase.towerClans.utils.colorizer.ColorizerProvider;

import java.util.List;
import java.util.stream.Collectors;

public class ClanInfoCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;
    private final static ColorizerProvider colorizerProvider;

    static  {
        colorizerProvider = Plugin.getColorizerProvider();
    }

    public ClanInfoCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.info");

        if(args.length < 2) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        String name = args[1];

        ClanImpl clan = ClanImpl.getClans().get(name);
        if(clan == null) {
            chatUtil.sendMessage(player, configurationSection.getString("there_is_no_such_clan"));
            return true;
        }

        List<String> list = configurationSection.getStringList("information_about_the_clan");

        List<String> replacedList = list.stream().map(string -> {
            String replacedString = string
                    .replace("%name%", clan.getName())
                    .replace("%members%", String.valueOf(clan.getMembers().size()))
                    .replace("%level%", String.valueOf(clan.getLevel()))
                    .replace("%xp%", String.valueOf(clan.getXp()))
                    .replace("%balance%", String.valueOf(clan.getBalance()))
                    .replace("%kills%", String.valueOf(Stats.getKillsMembers(clan.getMembers())))
                    .replace("%deaths%", String.valueOf(Stats.getDeathMembers((clan.getMembers()))));
            return colorizerProvider.colorize(replacedString);
        }).collect(Collectors.toList());

        for(String string : replacedList) {
            chatUtil.sendMessage(player, string);
        }

        return true;
    }
}
